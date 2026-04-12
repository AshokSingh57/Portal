# Step 1 - Build Terraform Template

a. Build a terraform template agentinfrastructure.tf to configure a GCP Artifact Registry with following parameters:

1. Service account JSON Key file name: gen-lang-client-0133235876-d77559bc6b0c.json
2. Project ID: gen-lang-client-0133235876
3. repository_id: repo1
4. repository_type: Docker
5. Location: us-central1
6. Repository mode: standard

The JSON Key file name should be stored in a properties file.

# Step 2 - Validate Terraform

Run Terraform validate to validate the terraform template

# Step 3 - Plan Terraform

Run Terraform plan to plan the terraform template

# Step 4 Apply Terraform

Run Terraform apply to exexute the terraform template

# Step 5 - Build Docker file

1. Create Docker file with basic linux image
2. Keep port 8080 open for public port access
3. Copy privatekey.pem, publickey.pem into the container

Step 6 - Build the container

1. Run GCP Cloud Build to compile on GCP and build the container on GCP and to store it in GCP Artifact Factory repo1 repository with the container name Portal

---

# Step 6 - Deploy on Cloud Run

1. Deploy the container Portal to GCP Cloud Run with port 80 of the Cloud Run open for internet access.

# Step 7 - Validate the application
Run all of the following items 1 through 4:
1. Connect to`<Cloud Run service public URL>`/api and confirm login page
2. Login as admin@myexample.com/Mynewcadillac1@ and check for successful login
3. Click on the "Admin" link and validate the number of users in the list box exceed 2.
4. Logout of the application by clicking on the "Logout" link.

# Step 8 - Destroy the infrastructure

1. Delete the GCP Artifact Registry repository repo1
2. Stop and delete the GCP Cloud Run Portal service

# Step 9 - Wrap up

Print the time taken to accomplish above operations


---



---



---
