# Maatram Foundation Volunteer Hub

This repository contains the existing responsive Maatram Foundation frontend and the
initial Spring Boot backend foundation. The frontend demo remains available and continues
to use localStorage until the later API-integration phase.

The backend uses Java 17, Spring Boot, Spring Security, Spring Data JPA, Hibernate, and
MySQL. It intentionally does not contain database credentials.

## Run the frontend

Open `index.html` directly in a browser, or serve this folder with a static web server:

```powershell
python -m http.server 5173
```

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

## Run the Spring Boot backend

Set these environment variables before starting the backend:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/maatram?createDatabaseIfNotExist=true&serverTimezone=UTC"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-password"
```

Run the API from the project root:

```powershell
# Portable tools installed in .tools when system-wide installation is unavailable
$env:JAVA_HOME = "$PWD\.tools\jdk-17.0.21+9"
$env:Path = "$env:JAVA_HOME\bin;$PWD\.tools\apache-maven-3.9.9\bin;$env:Path"
mvn spring-boot:run
```

The health endpoint is available at `http://localhost:8080/api/health`.
Volunteer registration is `POST /api/auth/register`. Authenticated requests currently
use HTTP Basic authentication with the registered email and password. Administrative
zone and college endpoints are protected by the `SUPER_ADMIN` role.

## Deploy the backend

The repository includes [Dockerfile](./Dockerfile) and [render.yaml](./render.yaml)
for a Render web service. The default deployment uses a file-based H2 database, so
no paid MySQL service is required for a demo. Wait for `/api/health` to pass. For
production persistence, set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` for a
reachable MySQL database. Then set the deployed API URL plus `/api` in
[api-config.js](./api-config.js), for example
`https://your-api.example.com/api`, commit, and push that change so GitHub Pages can
reach the backend.

The persistent MySQL APIs now include:

- `GET /api/announcements` for published Home/Events announcements.
- `POST`, `PUT`, `PATCH /api/announcements/admin...`, and `DELETE /api/announcements/admin/{id}` for Super Admin announcement management.
- `POST /api/contributions` and `GET /api/contributions/mine` for volunteer contribution posts.
- `GET /api/contributions/mine/achievement` for independent School Visit and Physical Verification levels.
- `PATCH /api/contributions/{id}/status` for Zone In-charge/Super Admin verification and automatic achievement updates.

No database credentials or private keys should be committed to this repository.
