# Setting Up this Project with Git

This guide will walk you through cloning the project from GitHub and setting it up for development in your preferred IDE.

### Prerequisites

Before you begin, ensure you have the following installed:
*   **Git:** [Download & Install Git](https://git-scm.com/downloads)
*   **IDE & Extension Pack:** Your preferred Integrated Development Environment.
    *   **Apache NetBeans:** [Download NetBeans](https://netbeans.apache.org/download/index.html) (Recommended for this project)
    *   **Visual Studio Code:** [Download VS Code](https://code.visualstudio.com/download) with the Java extensions.
    *   **Extension Pack for Java:** If you are Using VS Code, go to the Extensions view (`Ctrl+Shift+X`) and install the `Extension Pack for Java` This is essential for Java language support.

***

## For NetBeans Users (Recommended)

This project is a native NetBeans project.

### Step 1: Clone the Project from GitHub

1.  **Open NetBeans.**
2.  Go to the main menu and select `Team > Git > Clone`.

    

3.  In the "Clone Repository" window, fill in the details:
    *   **Repository URL:** Paste the `HTTPS` URL of the GitHub repository. You can get this by clicking the green "<> Code" button on the GitHub page.
    *   **User:** Your GitHub username.
    *   **Password:**
        > **Important:** GitHub no longer accepts passwords for Git operations. You **must** use a **Personal Access Token (PAT)**.
        > *   [Follow this official GitHub guide to create a PAT](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token).
        > *   When creating the token, make sure to grant it the `repo` scope.
        > *   Copy the generated token and paste it into the "Password" field in NetBeans.

4.  Click **Next**.
5.  Select the branch you want to work on usually `main` but choice `development`. Click **Next**.
6.  Choose a local folder on your computer to save the project. Keep the "Clone Name" and "Checkout Branch" as they are.
7.  Click **Finish**.

8.  NetBeans will download the project. Once it's done, a dialog will appear. Click **Open Project** to load it into the IDE.

### Step 2: Making and Committing Changes

1.  **Work on the code.** When you edit a file, its name will turn **blue** in the "Projects" view, indicating it has been modified. A new file will be **green**.
2.  When you're ready to save a snapshot of your work, **right-click** on the project root folder in the "Projects" view.
3.  Go to `Git > Commit`.
4.  In the "Commit" window:
    *   Please write a clear and concise **Commit Message** (e.g., "feat: Implement live bracket preview").
    *   The files you changed will be listed. Make sure the ones you want to include are checked.
    *   Click the **Commit** button.

### Step 3: Pushing and Pulling Changes

*   **Push (Send your changes to GitHub):**
    1.  **Right-click** the project.
    2.  Go to `Git > Remote > Push...`.
    3.  Follow the prompts, authenticating with your username and Personal Access Token if asked.

*   **Pull (Get the latest changes from GitHub):**
    1.  **Right-click** the project.
    2.  Go to `Git > Remote > Pull...`.
    3.  This will update your local project with any new commits from other contributors.

***

## For Visual Studio Code Users 

This is a NetBeans-based project, but you can build, run, and contribute to it using VS Code.

> **Important Note:** This project contains a `nbproject` folder with NetBeans-specific configurations. Please **do not delete or modify this folder**, as it is required for other developers using NetBeans.

### Step 1: Clone and Open the Project

1.  Open your system's terminal (like Command Prompt, PowerShell, Bash, or VSCode).
2.  Navigate to the directory where you want to store your projects.
    ```bash
    cd path/to/your/workspace
    ```
3.  Clone the repository from GitHub using the `git clone` command.
    ```bash
    git clone [https://github.com/your-username/BracketCraft.git]
    ```
4.  Navigate into the newly created project folder.
    ```bash
    cd BracketCraft
    ```
5.  Open the folder in VS Code.
    ```bash
    code .
    ```

### Step 2: Build and Run 

    In the VSCode, navigate to and open the main entry point of the application: src/bracketcraft/MainFrame.java.
    You will see small buttons appear above the main method.
    Click the Run button to build and start the application.


### Step 3: Git Workflow from the Terminal

Here is the complete command-line process for managing your changes.

1.  **Check the status** of your changes at any time.
    ```bash
    git status
    ```

2.  **Add your changed files** to the staging area. This prepares them for a commit.
    ```bash
    # To add all changed files in the project
    git add .
    
    # Or, to add a specific file
    git add src/bracketcraft/MainFrame.java
    ```

3.  **Commit your staged changes** with a clear, descriptive message.
    ```bash
    git commit -m "feat: Add interactive score-entry dialog"
    ```

4.  **Pull the latest changes** from the GitHub repository before you push. This prevents conflicts.
    ```bash
    git pull
    ```

5.  **Push your committed changes** to GitHub.
    ```bash
    git push
    ```
