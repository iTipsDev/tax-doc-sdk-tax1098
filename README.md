# tax-doc-sdk-tax1098

https://github.com/iTipsDev/tax-doc-sdk-tax1098

Java software development kit to generate IRS Form 1098, Mortgage Interest Statement, in portable document format (PDF)

Supports production of PDF files with embedded JSON, also known as [Intelligent Tax Documents](https://www.taxdataexchange.org/intelligent) :registered:

Supports production of PDF files with QR codes

Supports CSV file as input

Uses Financial Data Exchange (FDX) tax version 6.3.0 standard data structures

<img alt="Sample Form" src="output/company1/000001/000001.download.png" width="850"/>

This GitHub project is public but the Maven repository on which it relies is private

Go to www.taxdataexchange.org/services/index.html to purchase a subscription to use the SDK and related Maven repository


### Maven Artifacts - Google Artifact Registry Access

Access to the Maven artifacts in Google Artifact Registry require the following, one-time, set up.

Upon ordering the SDK, you indicate the email of the developer or the email of the Google service account that will be using the artifact. We grant the permission necessary to access the repository to that account.



### Obtain Credentials --> JSON File --> Environment Variable
 
Obtain a json file with the credentials corresponding to the developer or service account email (see below)
 
Create a GOOGLE_APPLICATION_CREDENTIALS environment variable that points to the file. For example:

```
export GOOGLE_APPLICATION_CREDENTIALS='/Users/johnjones/.config/gcloud/application_default_credentials.json'
```

### Getting Credentials for Your Google Account (for Your Email Address)

At a terminal prompt, type

```
gcloud auth application-default login
```

The gcloud program will prompt you to authorize creation of a credentials file. 

It will then save the file and display the path to the file.


### GCloud CLI 

The above assumes you have installed the Google Cloud Command Line Interface (CLI) on your system.

See the installation instructions at:

https://cloud.google.com/sdk/docs/install


