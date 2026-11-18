create extension if not exists pgcrypto;

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  name text not null,
  register_number text not null,
  college_name text not null,
  department text not null,
  year text not null,
  zone text not null,
  zone_incharge_name text not null default '',
  email text not null,
  contact_number text not null,
  profile_photo_url text,
  role text not null default 'VOLUNTEER' check (role in ('VOLUNTEER', 'ORGANISER', 'ZONE_INCHARGE', 'SUPER_ADMIN')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint profiles_register_number_key unique (register_number),
  constraint profiles_email_key unique (email)
);

create table if not exists public.volunteer_requirements (
  id uuid primary key default gen_random_uuid(),
  event_name text not null,
  event_description text not null,
  event_date date not null,
  event_time time not null,
  location text not null,
  required_volunteer_count integer not null check (required_volunteer_count > 0),
  volunteer_instructions text not null default '',
  skills_or_eligibility text not null default '',
  registration_deadline date,
  image_url text,
  published boolean not null default false,
  closed boolean not null default false,
  created_by uuid references public.profiles(id),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.availability_responses (
  id uuid primary key default gen_random_uuid(),
  requirement_id uuid not null references public.volunteer_requirements(id) on delete cascade,
  student_id uuid not null references public.profiles(id) on delete cascade,
  response text not null check (response in ('AVAILABLE', 'NOT_AVAILABLE')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint availability_responses_requirement_student_key unique (requirement_id, student_id)
);

create table if not exists public.volunteer_contributions (
  id uuid primary key default gen_random_uuid(),
  student_id uuid not null references public.profiles(id) on delete cascade,
  event_name text not null,
  activity_type text,
  description text,
  photo_urls text[] not null default '{}',
  beneficiaries integer not null default 0,
  status text not null default 'PENDING' check (status in ('PENDING', 'APPROVED', 'REJECTED')),
  hours numeric not null default 0,
  points integer not null default 0,
  created_at timestamptz not null default now()
);

create or replace function public.is_admin()
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.profiles
    where id = auth.uid() and role = 'SUPER_ADMIN'
  );
$$;

create or replace function public.is_admin_or_manager()
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.profiles
    where id = auth.uid() and role in ('SUPER_ADMIN', 'ORGANISER', 'ZONE_INCHARGE')
  );
$$;

create or replace function public.requirement_response_summary(p_requirement_id uuid)
returns table(available_count bigint, unavailable_count bigint)
language sql
stable
security definer
set search_path = public
as $$
  select
    count(*) filter (where response = 'AVAILABLE'),
    count(*) filter (where response = 'NOT_AVAILABLE')
  from public.availability_responses
  where requirement_id = p_requirement_id;
$$;

alter table public.profiles enable row level security;
alter table public.volunteer_requirements enable row level security;
alter table public.availability_responses enable row level security;
alter table public.volunteer_contributions enable row level security;

drop policy if exists "profiles_select_own_or_admin" on public.profiles;
create policy "profiles_select_own_or_admin"
on public.profiles for select
to authenticated
using (id = auth.uid() or public.is_admin_or_manager());

drop policy if exists "profiles_update_own" on public.profiles;
create policy "profiles_update_own"
on public.profiles for update
to authenticated
using (id = auth.uid())
with check (id = auth.uid() and role = (select role from public.profiles where id = auth.uid()));

drop policy if exists "profiles_insert_own" on public.profiles;
create policy "profiles_insert_own"
on public.profiles for insert
to authenticated
with check (id = auth.uid());

drop policy if exists "requirements_public_read" on public.volunteer_requirements;
create policy "requirements_public_read"
on public.volunteer_requirements for select
to anon, authenticated
using (published = true);

drop policy if exists "requirements_admin_manage" on public.volunteer_requirements;
create policy "requirements_admin_manage"
on public.volunteer_requirements for all
to authenticated
using (public.is_admin())
with check (public.is_admin());

drop policy if exists "responses_own_manage" on public.availability_responses;
create policy "responses_own_manage"
on public.availability_responses for all
to authenticated
using (student_id = auth.uid())
with check (student_id = auth.uid());

drop policy if exists "responses_admin_read" on public.availability_responses;
create policy "responses_admin_read"
on public.availability_responses for select
to authenticated
using (public.is_admin());

drop policy if exists "contributions_own_manage" on public.volunteer_contributions;
create policy "contributions_own_manage"
on public.volunteer_contributions for all
to authenticated
using (student_id = auth.uid())
with check (student_id = auth.uid());

drop policy if exists "contributions_admin_read" on public.volunteer_contributions;
create policy "contributions_admin_read"
on public.volunteer_contributions for select
to authenticated
using (public.is_admin_or_manager());

grant usage on schema public to anon, authenticated;
grant select on public.volunteer_requirements to anon, authenticated;
grant select, insert, update on public.profiles to authenticated;
grant select, insert, update, delete on public.volunteer_requirements to authenticated;
grant select, insert, update, delete on public.availability_responses to authenticated;
grant select, insert, update on public.volunteer_contributions to authenticated;
grant execute on function public.requirement_response_summary(uuid) to anon, authenticated;

insert into storage.buckets (id, name, public)
values ('profile-photos', 'profile-photos', true)
on conflict (id) do nothing;

insert into storage.buckets (id, name, public)
values ('announcement-images', 'announcement-images', true)
on conflict (id) do nothing;

insert into storage.buckets (id, name, public)
values ('volunteer-contributions', 'volunteer-contributions', true)
on conflict (id) do nothing;

drop policy if exists "contribution_storage_upload" on storage.objects;
create policy "contribution_storage_upload"
on storage.objects for insert
to authenticated
with check (bucket_id = 'volunteer-contributions');

drop policy if exists "contribution_storage_read" on storage.objects;
create policy "contribution_storage_read"
on storage.objects for select
to anon, authenticated
using (bucket_id = 'volunteer-contributions');

drop policy if exists "announcement_storage_upload" on storage.objects;
create policy "announcement_storage_upload"
on storage.objects for insert
to authenticated
with check (bucket_id = 'announcement-images' and public.is_admin());

drop policy if exists "announcement_storage_read" on storage.objects;
create policy "announcement_storage_read"
on storage.objects for select
to anon, authenticated
using (bucket_id = 'announcement-images');
