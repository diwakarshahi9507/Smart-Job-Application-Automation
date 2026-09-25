# JobReach — Smart Job Application & Recruiter Outreach Platform

JobReach is a Spring Boot based recruiter outreach automation platform that helps users import recruiter data, manage reusable email templates, send personalized emails, create campaigns, track delivery status, analyze outreach performance, and manage email settings from one dashboard.

## Features

- Excel-based recruiter import using Apache POI
- Recruiter validation and duplicate detection
- Recruiter soft delete
- Database-backed email templates
- Placeholder-based personalization:
  - `{{firstName}}`
  - `{{company}}`
  - `{{position}}`
- Direct email sending to selected recruiters
- HTML email support
- Automatic PDF resume attachment
- Campaign creation and bulk outreach
- Campaign recipient tracking
- Retry failed campaign recipients
- Error message and attempt-time tracking
- Analytics for recruiter and campaign delivery
- Settings module for sender details, default position, email signature, and resume
- Resume PDF upload and replacement

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- JavaMailSender

### Frontend
- Thymeleaf
- HTML5
- CSS3

### Database
- PostgreSQL

### File Processing
- Apache POI
- MultipartFile

### Tools
- Maven
- Git
- GitHub
- Postman
- IntelliJ IDEA / Eclipse

## Architecture

```text
Thymeleaf UI
    ↓
Controllers
    ↓
Services
    ↓
Repositories
    ↓
PostgreSQL

EmailService
    ↓
SMTP Provider
```

Main controllers:

```text
DashboardController
RecruiterController
CampaignController
TemplateController
AnalyticsController
SettingsController
```

Main services:

```text
RecruiterService
CampaignService
CampaignRecipientService
EmailTemplateService
EmailService
TemplateService
ExcelService
SettingsService
```

## Main Modules

### Recruiter Management

Recruiters can be imported from an Excel file with the following format:

| FirstName | Email | Company | Position |
|---|---|---|---|
| Rahul | rahul@example.com | ABC Tech | Java Developer |

Recruiter records support:

```text
PENDING
SENT
FAILED
```

Soft deletion is implemented using an `active` flag.

### Email Template Management

Templates are stored in PostgreSQL and support dynamic placeholders:

```text
{{firstName}}
{{company}}
{{position}}
```

Example:

```html
Dear {{firstName}},

I am interested in the {{position}} opportunity at {{company}}.

Regards,
Your Name
```

Templates can be created, edited, and soft-deleted.

### Direct Recruiter Email

Users can:

- Select multiple recruiters
- Select a stored template
- Send personalized HTML emails
- Attach the configured resume automatically
- Track the latest send status for each recruiter

### Campaign Management

Campaigns support:

- Campaign name
- Target position
- Selected email template
- Multiple selected recruiters
- Recipient-level delivery tracking
- Retry of failed recipients

Campaign states currently include:

```text
CREATED
COMPLETED
PARTIAL
FAILED
```

### Campaign Recipient Tracking

Each campaign-recipient combination stores:

```text
status
attemptTime
sentTime
errorMessage
```

A unique database constraint prevents duplicate recruiter entries inside the same campaign.

### Analytics

The analytics module displays:

- Total active recruiters
- Sent recruiters
- Failed recruiters
- Pending recruiters
- Recruiter success rate
- Total campaigns
- Campaign email records
- Campaign sent count
- Campaign failed count
- Campaign pending count
- Campaign success rate

Progress bar colors:

- Green — Sent
- Red — Failed
- Yellow — Pending

### Settings

The Settings module stores:

```text
senderName
senderEmail
resumePath
defaultPosition
emailSignature
```

These values are saved in PostgreSQL and used by the email workflow.

### Resume Upload

Users can upload a PDF resume from the Settings page.

Flow:

```text
Select PDF
    ↓
Upload
    ↓
Save under uploads/resumes
    ↓
Update resumePath in app_settings
    ↓
Delete previous resume
    ↓
Attach new resume to future emails
```

## Database Tables

### recruiters

| Column | Description |
|---|---|
| id | Primary key |
| first_name | Recruiter first name |
| email | Unique recruiter email |
| company | Company |
| position | Target position |
| status | Latest email status |
| sent_time | Latest successful send time |
| active | Soft-delete flag |

### email_templates

| Column | Description |
|---|---|
| id | Primary key |
| name | Unique template name |
| subject | Email subject |
| body | HTML body |
| active | Soft-delete flag |
| created_time | Created timestamp |
| updated_time | Updated timestamp |

### campaigns

| Column | Description |
|---|---|
| id | Primary key |
| campaign_name | Campaign name |
| position | Campaign position |
| template_id | Email template reference |
| recruiter_count | Recruiter count |
| status | Campaign status |
| created_time | Creation time |

### campaign_recipients

| Column | Description |
|---|---|
| id | Primary key |
| campaign_id | Campaign reference |
| recruiter_id | Recruiter reference |
| status | Delivery status |
| sent_time | Successful send time |
| attempt_time | Latest attempt |
| error_message | Failure reason |

### app_settings

| Column | Description |
|---|---|
| id | Primary key |
| sender_name | Sender display name |
| sender_email | Sender email |
| resume_path | Active resume location |
| default_position | Default job position |
| email_signature | Email signature |

## Routes

| Route | Method | Purpose |
|---|---|---|
| `/dashboard` | GET | Main dashboard |
| `/recruiters` | GET | Recruiter list |
| `/recruiters/import` | POST | Import recruiters |
| `/recruiters/send-selected` | POST | Send direct email |
| `/recruiters/delete/{id}` | POST | Soft-delete recruiter |
| `/campaigns` | GET | Campaign management |
| `/campaigns/create` | POST | Create campaign |
| `/campaigns/send` | POST | Send campaign |
| `/campaigns/{id}` | GET | Campaign details |
| `/campaigns/{id}/retry-failed` | POST | Retry failed recipients |
| `/templates` | GET | Template management |
| `/templates/create` | POST | Create template |
| `/templates/edit/{id}` | GET | Edit template |
| `/templates/update` | POST | Update template |
| `/templates/delete/{id}` | POST | Soft-delete template |
| `/analytics` | GET | Analytics dashboard |
| `/settings` | GET | Settings page |
| `/settings` | POST | Save settings |
| `/settings/upload-resume` | POST | Upload/replace resume |

## Getting Started

### Prerequisites

Install:

- Java 21
- Maven
- PostgreSQL
- Git
- SMTP-enabled email account

### Clone Repository

```bash
git clone <your-repository-url>
cd Smart-Job-Application-Automation
```

### Create Database

```sql
CREATE DATABASE smart_job;
```

### Configure Database

In `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smart_job
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Configure Email

Example Gmail SMTP configuration:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Use environment variables for credentials. Never commit real passwords or app passwords to GitHub.

### Run Application

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080/dashboard
```

## Typical Workflow

```text
Configure Settings
    ↓
Upload Resume
    ↓
Create Email Template
    ↓
Import Recruiters
    ↓
Send Direct Email
        or
Create Campaign
    ↓
Track SENT / FAILED / PENDING
    ↓
Retry Failed Campaign Emails
    ↓
Review Analytics
```

## Position Resolution

The effective job position follows this priority:

```text
Campaign / requested position
        ↓
Recruiter position
        ↓
Default position from Settings
        ↓
Empty value
```

## Project Status

Completed:

- [x] Recruiter Excel import
- [x] Duplicate recruiter handling
- [x] Recruiter soft delete
- [x] Database-backed templates
- [x] Template edit and soft delete
- [x] Personalized HTML emails
- [x] Resume attachment
- [x] Campaign creation
- [x] Campaign recipient tracking
- [x] Retry failed recipients
- [x] Attempt-time tracking
- [x] Error-message tracking
- [x] Recruiter analytics
- [x] Campaign analytics
- [x] Premium dark dashboard
- [x] Settings module
- [x] Resume PDF upload and replacement
- [x] Configurable sender details
- [x] Default position
- [x] Email signature

Planned:

- [ ] Direct email history
- [ ] Spring Security
- [ ] Global validation and exception handling
- [ ] Automated testing
- [ ] Flyway migrations
- [ ] Campaign scheduling
- [ ] Advanced analytics
- [ ] Multiple resume profiles
- [ ] Production deployment

## Planned Direct Email History

The next module will preserve each direct email attempt independently.

Planned entity:

```text
EmailActivity
-------------
id
recruiter_id
template_id
position
status
attempt_time
sent_time
error_message
```

## Security Notes

Before pushing the project publicly:

```text
Do not commit:
- Database passwords
- SMTP passwords
- Gmail App Passwords
- API keys
- Private credentials
```

Recommended `.gitignore` additions:

```gitignore
target/
.idea/
*.iml
.env
application-local.properties
uploads/
```

If credentials were ever committed previously, rotate them and remove them from Git history.

## Screenshots

Create a `screenshots/` directory and add:

```text
dashboard.png
recruiters.png
campaigns.png
templates.png
analytics.png
settings.png
```

Then reference them like:

```markdown
![Dashboard](screenshots/dashboard.png)
```

## Future Improvements

- Authentication and authorization
- Scheduled campaigns
- Email activity history
- Multiple resumes
- Template versioning
- Chart-based analytics
- REST APIs
- Flyway migrations
- Docker
- CI/CD
- Cloud deployment

## Author

**Diwakar Kumar**

Java Backend Developer

Java | Spring Boot | Hibernate / JPA | PostgreSQL | REST APIs | Git

## License

This project is currently intended for learning, portfolio, and demonstration purposes.

For public distribution, add an appropriate open-source license such as the MIT License.
