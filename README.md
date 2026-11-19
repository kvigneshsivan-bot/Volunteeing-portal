# Maatram Foundation Volunteer Hub

This repository contains the existing responsive Maatram Foundation frontend integrated
with Supabase Authentication and PostgreSQL.

The backend uses Java 25, Spring Boot, Spring Security, Spring Data JPA, Hibernate, and
MySQL. It intentionally does not contain database credentials.

## SMS broadcast configuration

SMS to registered volunteers uses Twilio. Configure these environment variables on the
machine or hosting platform; do not add credential values to source code:

- `TWILIO_ACCOUNT_SID`
- `TWILIO_AUTH_TOKEN`
- `TWILIO_FROM_NUMBER`

Contribution review email notifications use Resend. Add these environment variables to the
Render service (or attach the environment group containing them):

- `RESEND_API_KEY`
- `ADMIN_EMAIL` (defaults to `k.vigneshsivan@gmail.com`)
- `MAIL_FROM` (use a verified sender; Resend's `onboarding@resend.dev` works for initial testing)

The Admin dashboard's **SMS all volunteers** action sends to volunteers with saved
mobile numbers. Indian 10-digit numbers are normalized to `+91`.

## Run the frontend

Open `index.html` directly in a browser, or serve this folder with a static web server:

```powershell
python -m http.server 5173
```

## Deploy the backend for free on Render

The repository includes a Render Blueprint that uses the free web-service plan.
Deploy it from GitHub:

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/kvigneshsivan-bot/Volunteeing-portal)

In the Render deployment page:

1. Sign in to or create a free Render account.
2. Select the `maatram-api` service.
3. Keep the plan set to `Free`.
4. Deploy the Blueprint.
5. Copy the generated service URL, for example `https://maatram-api-xxxx.onrender.com`.

After deployment, set [api-config.js](./api-config.js) to the generated URL followed by `/api`
(for example, `https://maatram-api-xxxx.onrender.com/api`) and push the change.

The free service may sleep when idle. It can take 30–60 seconds to wake up on the next
request. The H2 demo database is ephemeral on Render's free tier; use a persistent database
if permanent server-side data is required.

The supplied Maatram Foundation logo is stored at `assets/maatram-logo.png`.

The current frontend supports a two-step first-time setup: email/password (or the Google
placeholder) first, followed by the Maatram profile details. It saves the profile locally
in the browser. Returning users can sign in with the saved email and password hash to
reopen the profile. The Google button is intentionally a configuration placeholder until
a Google OAuth Client ID is supplied; it does not fake Google authentication.
Each volunteer opportunity opens its own workspace where users can add an activity note,
upload at least three proof photos, and publish a contribution post. The browser demo
stores these posts locally until the frontend API-integration phase.
Published volunteer requests now include an `Available` action backed by the Spring Boot
API. Availability records store the volunteer name, mobile number, and academic year in
MySQL, show in the Admin dashboard, and automatically display `Request closed` when the
required count is reached. The frontend refreshes published announcements from the API so
other volunteer browsers can see newly published requests.

## Configure Supabase

1. Open the Supabase SQL Editor.
2. Run [supabase-migration.sql](./supabase-migration.sql).
3. In Supabase Authentication settings, enable email/password sign-in.
4. Keep [supabase-config.js](./supabase-config.js) pointed at the Supabase project.

The integration stores:

- Student profiles in `public.profiles`
- Volunteer requirements in `public.volunteer_requirements`
- Available/not available responses in `public.availability_responses`
- Volunteer contributions and proof image URLs in `public.volunteer_contributions`
- Announcement poster images in the public `announcement-images` storage bucket

The existing frontend still uses `localStorage` only as a visual cache. Supabase is the
persistent source for authentication, profiles, requirements, availability responses,
and student contributions.

No database password or service-role key is required in the frontend. Only the
publishable/anon key belongs in [supabase-config.js](./supabase-config.js).
