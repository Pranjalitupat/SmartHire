# 🎯 SmartHire — AI-Based Resume Analyzer & Job Matcher

## 📌 About
SmartHire is a Java Spring Boot web application that helps 
job seekers find suitable jobs based on their resume skills 
and helps HR managers find best candidates automatically.

## ✨ Features
- 📄 Resume Upload & PDF Parsing
- 🔍 AI-Based Skill Extraction
- 💼 Job Matching with Score %
- 📊 Skill Gap Analysis
- 💡 Learning Roadmap with Resource Links
- 👥 HR Module — Post Jobs & View Candidates
- 📥 PDF Report Download
- 🔐 Secure Login with BCrypt Encryption
- 📋 Job Application Tracking

## 🛠️ Tech Stack
| Technology | Purpose |
|---|---|
| Java 21 | Programming Language |
| Spring Boot 4 | Web Framework |
| MySQL | Database |
| Hibernate/JPA | ORM |
| Apache PDFBox | PDF Parsing |
| Thymeleaf | Template Engine |
| Bootstrap 5 | Frontend UI |
| Spring Security | Authentication |
| BCrypt | Password Encryption |
| iText | PDF Report Generation |

## 🔄 How it Works
1. User registers as Job Seeker or HR Manager
2. Job Seeker uploads resume in PDF or DOCX format
3. System extracts skills automatically using PDFBox
4. TF-IDF algorithm matches skills with job descriptions
5. Shows match score percentage and skill gap analysis
6. HR managers can post jobs and view ranked candidates
7. Download professional PDF report with complete analysis

## ⚙️ Setup Instructions

### Prerequisites
- Java 17+
- MySQL 8.0
- Maven
- XAMPP for local MySQL

### Steps

**1. Clone repository**

git clone https://github.com/Pranjalitupat/SmartHire.git

**2. Create MySQL database**

CREATE DATABASE smarthire;

**3. Configure application.properties**

spring.datasource.url=jdbc:mysql://localhost:3306/smarthire
spring.datasource.username=root
spring.datasource.password=
server.port=8085

**4. Run the application**

Right click SmarthireApplication.java
Run As → Spring Boot App

**5. Open browser**

http://localhost:8085

## 📁 Project Structure

smarthire/
├── src/main/java/com/smarthire/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── PasswordEncoderConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── ResumeController.java
│   │   ├── MatchController.java
│   │   ├── SkillGapController.java
│   │   ├── HRController.java
│   │   ├── ApplyController.java
│   │   └── ReportController.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Resume.java
│   │   ├── Job.java
│   │   ├── JobApplication.java
│   │   └── SkillGap.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ResumeRepository.java
│   │   ├── JobRepository.java
│   │   ├── JobApplicationRepository.java
│   │   └── SkillGapRepository.java
│   └── service/
│       ├── CustomUserDetailsService.java
│       ├── ResumeParserService.java
│       ├── MatchingService.java
│       ├── SkillGapService.java
│       └── ReportService.java
├── src/main/resources/
│   ├── templates/
│   │   ├── login.html
│   │   ├── register.html
│   │   ├── dashboard.html
│   │   ├── upload.html
│   │   ├── match.html
│   │   ├── skillgap.html
│   │   ├── apply-status.html
│   │   ├── my-applications.html
│   │   └── hr/
│   │       ├── dashboard.html
│   │       ├── post-job.html
│   │       └── candidates.html
│   └── application.properties
└── pom.xml

## 🗄️ Database Tables
| Table | Purpose |
|---|---|
| users | User accounts and roles |
| resumes | Uploaded resume data and extracted skills |
| jobs | Job postings with required skills |
| job_applications | Candidate job applications |
| match_results | Job match scores |
| skill_gap | Missing skills with learning resources |
| job_suggestions | Suggested jobs for candidates |
| skills_master | Master list of technical skills |

## 🔐 User Roles

### Job Seeker
- Register and login securely
- Upload resume in PDF or DOCX format
- View extracted skills automatically
- See top 5 job matches with score percentage
- Analyze skill gap with learning roadmap
- Apply for jobs directly
- Track application status
- Download PDF report

### HR Manager
- Register and login as HR
- Post new job openings with required skills
- View all candidates ranked by match score
- Shortlist, select or reject candidates
- Manage job postings

## 💡 Algorithm
SmartHire uses TF-IDF based skill matching algorithm:
- Extracts text from resume PDF using Apache PDFBox
- Identifies skills by matching against master skills list
- Compares resume skills with job required skills
- Calculates match score = matched skills / total required x 100
- Ranks all jobs by match score in descending order
- Shows top 5 best matching jobs
- Identifies missing skills as skill gap
- Provides learning resource links for each missing skill

## 🔗 Project Links
- GitHub: https://github.com/Pranjalitupat/SmartHire
- Developer: Pranjali Tupat

## 👩‍💻 Developer
**Pranjali Tupat**
MCA Final Year Student
GitHub: https://github.com/Pranjalitupat

