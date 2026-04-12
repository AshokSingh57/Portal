# Deployment

## Local Development

### Prerequisites
- Java 17+
- Maven 3.9+
- Provisioner service running on port 8081

### Run

```bash
# From project root
mvn spring-boot:run

# With custom Provisioner URL
mvn spring-boot:run -Dspring-boot.run.arguments=--provisioner.base-url=http://myhost:8081/api
```

Access: `http://localhost:8080/api/login`

### Default Credentials (via Provisioner)
- Email: `admin@myexample.com`
- Password: `Mynewcadillac1@`

---

## Docker

### Dockerfile (Multi-Stage Build)

**Stage 1 — Build:**
- Base: `eclipse-temurin:17-jdk-alpine`
- Installs Maven, runs `mvn clean package -DskipTests`

**Stage 2 — Runtime:**
- Base: `eclipse-temurin:17-jre-alpine`
- Copies JAR from build stage
- Exposes port 8080

### Build & Run

```bash
# Build image
docker build -t portal:latest .

# Run with default Provisioner URL
docker run -p 8080:8080 portal:latest

# Run with custom Provisioner URL
docker run -p 8080:8080 \
  -e PROVISIONER_BASE_URL=http://provisioner:8081/api \
  portal:latest
```

### Note on Dockerfile
The current Dockerfile still contains `COPY privatekey.pem` and `COPY publickey.pem` lines from the original monolith. These should be removed since the PEM files no longer exist and are not needed.

---

## Google Cloud Platform (Terraform)

**File:** `agentinfrastructure.tf`

Provisions a Google Artifact Registry repository for Docker images:

```hcl
resource "google_artifact_registry_repository" "repo1" {
  location      = "us-central1"
  repository_id = "repo1"
  format        = "DOCKER"
}
```

### Usage

```bash
# Initialize Terraform
terraform init

# Plan
terraform plan \
  -var="credentials_file=path/to/key.json" \
  -var="project_id=my-gcp-project"

# Apply
terraform apply \
  -var="credentials_file=path/to/key.json" \
  -var="project_id=my-gcp-project"
```

### Pushing to Artifact Registry

```bash
# Tag image
docker tag portal:latest us-central1-docker.pkg.dev/PROJECT_ID/repo1/portal:latest

# Push
docker push us-central1-docker.pkg.dev/PROJECT_ID/repo1/portal:latest
```

---

## Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `PROVISIONER_BASE_URL` | No | `http://localhost:8081/api` | Provisioner backend URL |
| `SERVER_PORT` | No | `8080` | Portal HTTP port |
| `SERVER_SERVLET_SESSION_TIMEOUT` | No | `24h` | Session timeout |
