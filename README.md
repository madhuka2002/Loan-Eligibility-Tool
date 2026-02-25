
# 🏦 Loan Eligibility Tool

![Java](https://img.shields.io/badge/Java-100%25-ED8B00?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-brightgreen)

A robust backend micro-loan detection engine. This tool processes user data against a predefined set of financial criteria to systematically and efficiently determine loan eligibility. 

## ⚙️ Core Features

* **Algorithmic Evaluation:** Contains the core logic to parse applicant attributes and compute eligibility status without manual intervention.
* **Data-Driven Processing:** Designed to ingest and process localized applicant data directly from the `data/input/` directory.
* **Seamless Build Process:** Fully managed via Maven, utilizing Maven Wrappers (`mvnw`) to guarantee consistent builds across any environment without requiring a pre-installed Maven instance.
* **Clean Architecture:** Strict separation of core logic (`src/main/`) from configuration and datasets, ensuring the foundational system is easy to maintain and scale.

## 📂 Repository Structure

```text
.
├── data/
│   └── input/               # Directory for raw applicant data files (e.g., CSV/JSON)
├── src/
│   └── main/                # Core Java source code and eligibility algorithms
├── .idea/                   # IDE configuration files
├── .gitignore               # Ignored files and directories
├── pom.xml                  # Maven project object model and dependencies
├── dependency-reduced-pom.xml 
├── mvnw                     # Unix Maven wrapper
├── mvnw.cmd                 # Windows Maven wrapper
└── README.md                # Project documentation

```

## 🚀 Getting Started

### Prerequisites

To compile and run this application, you will need:

* **Java Development Kit (JDK):** Version 11 or higher recommended.
* *(Optional)* An IDE like IntelliJ IDEA or Eclipse.

### Installation & Build

1. **Clone the repository:**
```bash
git clone [https://github.com/SithilSemitha/Loan-Eligibility-Tool.git](https://github.com/SithilSemitha/Loan-Eligibility-Tool.git)
cd Loan-Eligibility-Tool

```


2. **Provide Input Data:**
Place your applicant data files into the `data/input/` directory as expected by the application logic.
3. **Build the project:**
Use the included Maven wrapper to cleanly compile the code and build the executable JAR.
*On Linux/macOS:*
```bash
./mvnw clean install

```


*On Windows:*
```cmd
mvnw.cmd clean install

```



### Execution

Once built, you can run the tool via the command line. *(Note: Adjust the target `.jar` filename if your `pom.xml` specifies a different artifact ID or version).*

```bash
java -jar target/loan-eligibility-tool-1.0-SNAPSHOT.jar

```

## 🛠️ Tech Stack

* **Language:** Java
* **Dependency & Build Management:** Apache Maven
* **Paradigm:** Object-Oriented Programming (OOP), Data Processing
