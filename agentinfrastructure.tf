terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }
}

variable "credentials_file" {
  description = "Path to the GCP service account JSON key file"
  type        = string
}

variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

provider "google" {
  credentials = file(var.credentials_file)
  project     = var.project_id
  region      = "us-central1"
}

resource "google_artifact_registry_repository" "repo1" {
  location      = "us-central1"
  repository_id = "repo1"
  description   = "Docker repository for Portal"
  format        = "DOCKER"
  mode          = "STANDARD_REPOSITORY"
}

output "repository_url" {
  description = "URL of the created Artifact Registry repository"
  value       = google_artifact_registry_repository.repo1.name
}
