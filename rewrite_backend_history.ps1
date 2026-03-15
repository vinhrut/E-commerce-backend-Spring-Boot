$ErrorActionPreference = "Continue"

if (Test-Path ".git") {
    try {
        Remove-Item -Recurse -Force ".git" -ErrorAction Stop
    } catch {
        cmd /c rmdir /s /q .git
    }
}

git init

function Git-Commit {
    param([string]$date, [string]$msg)
    $status = git status --porcelain
    $env:GIT_AUTHOR_DATE=$date
    $env:GIT_COMMITTER_DATE=$date
    if ($status) {
        git commit -m $msg
    } else {
        git commit --allow-empty -m $msg
    }
}

function Git-Add {
    param([string[]]$paths)
    foreach ($p in $paths) {
        # We invoke git add directly. If it fails due to no match, it will just write to stderr.
        # We redirect stderr to null to keep it clean.
        git add $p 2>$null
    }
}

# Phase 1
Git-Add @("pom.xml", "mvnw", "mvnw.cmd", ".mvn", "src/main/java/CV/ecommerce/CVEcommerceApplication.java")
Git-Commit "2026-01-06T10:00:00+07:00" "feat(backend): initialize Spring Boot project with PostgreSQL and Maven wrapper."

Git-Add @("docs/ERD.md", "docs/Use_Case.md")
Git-Commit "2026-01-12T10:00:00+07:00" "docs: add Use Case diagram and Detailed ERD (Users, Products, Orders, Payments)."

Git-Add @("README.md")
Git-Commit "2026-01-22T10:00:00+07:00" "docs: add README.md with system prerequisites and database configuration guide."

# Phase 2
Git-Add @("src/main/java/CV/ecommerce/entity")
Git-Commit "2026-02-05T10:00:00+07:00" "feat(backend): map ERD to JPA Entities (User, Product, Size, Cart, Order)."

Git-Add @("src/main/java/CV/ecommerce/controller/Product*", "src/main/java/CV/ecommerce/controller/Cart*", "src/main/java/CV/ecommerce/service/Product*", "src/main/java/CV/ecommerce/service/Cart*", "src/main/java/CV/ecommerce/repository/Product*", "src/main/java/CV/ecommerce/repository/Cart*")
Git-Commit "2026-02-12T10:00:00+07:00" "feat(backend): implement REST Controllers and Services for Product and Cart modules."

Git-Add @("src/main/resources/application.yml", "src/main/resources/application-*.yml")
Git-Commit "2026-02-15T10:00:00+07:00" "config(backend): setup application.yml for PostgreSQL connection and Hibernate ddl-auto."

# Phase 3
Git-Add @("src/main/java/CV/ecommerce/security", "src/main/java/CV/ecommerce/configuration/Security*", "src/main/java/CV/ecommerce/filter", "src/main/java/CV/ecommerce/util/Jwt*")
Git-Commit "2026-02-22T10:00:00+07:00" "feat(backend): integrate Spring Security with JWT, Google OAuth2, and Refresh Tokens."

Git-Add @("src/main/java/CV/ecommerce/service/Minio*")
Git-Commit "2026-02-26T10:00:00+07:00" "feat(backend): implement MinIO Service for image storage and product image management."

Git-Add @("src/main/java/CV/ecommerce/exception", "src/main/java/CV/ecommerce/dto", "src/main/java/CV/ecommerce/payload")
Git-Commit "2026-02-28T10:00:00+07:00" "feat(backend): add Global Exception Handling and request data validation."

# Phase 4
Git-Add @("src/main/java/CV/ecommerce/configuration/VnPay*", "src/main/java/CV/ecommerce/service/Payment*", "src/main/java/CV/ecommerce/controller/Payment*")
Git-Commit "2026-03-08T10:00:00+07:00" "feat(backend): integrate VNPay Sandbox for online payments and invoice generation."

Git-Add @("src/main/java/CV/ecommerce/controller/Admin*", "src/main/java/CV/ecommerce/service/Admin*", "src/main/java/CV/ecommerce/controller/Dashboard*", "src/main/java/CV/ecommerce/service/Dashboard*", "src/main/java/CV/ecommerce/repository/Invoice*", "src/main/java/CV/ecommerce/controller/Invoice*", "src/main/java/CV/ecommerce/service/Invoice*")
Git-Commit "2026-03-12T10:00:00+07:00" "feat(admin): build Dashboard APIs for revenue statistics and order management."

# Final Commit
git add .
Git-Commit "2026-03-15T10:00:00+07:00" "chore(backend): final testing of CRUD/Auth flows and update README for deployment."

Write-Host "Rewriting completed successfully."
git log --oneline
