
withCredentials([usernamePassword(credentialsId: 'JIRAONCLOUD', passwordVariable: 'jiraapitok', usernameVariable: 'jirauser')]){
sh """cd /usr/bin/
curl --proxy \'http://appproxy.rbsgrp.net:8080\' --request POST \\
--url ${jurlAttach1} \\
-H "Content-Type: application/json" \\
--user "${jirauser}:${jiraapitok}" \\
-F "file=@/jenkins/jenkinsECP/output/"${apnd}"/"${params.SQ_Project}-${env.BUILD_NUMBER}".zip" """
}



/*
-> "String interpolation" warning in the code you posted is because you are using ${variable} syntax inside a Groovy String,
which can potentially be insecure if the variable contains sensitive information.

-> Jenkins is warning you about this because anyone with access to the Jenkins logs could potentially see the values of these variables,
which could be a security risk if they contain sensitive information such as passwords or API tokens.

In your specific case, you are using the jirauser and jiraapitok variables inside the --user option of the curl command.
These variables contain sensitive information (i.e., the JIRA username and API token),
so using String interpolation to include them in the command can be a security risk.

To avoid this warning, you can use single quotes instead of double quotes in your sh command. 
Using single quotes will prevent Groovy from performing String interpolation, which means that the ${variable} syntax will be treated as a literal string and not replaced with its value.

Alternatively, you can use the Jenkins Credential Binding plugin to securely pass the JIRA username and API token to the curl command, as shown in the code examples I provided earlier. This will ensure that the sensitive information is securely stored and not exposed in the Jenkins logs or build output.
*/

//SOL

withCredentials([usernamePassword(credentialsId: 'JIRAONCLOUD', passwordVariable: 'jiraapitok', usernameVariable: 'jirauser')]) {
    sh 'cd /usr/bin/ && \
        curl --proxy \'http://appproxy.rbsgrp.net:8080\' --request POST \
        --url ' + jurlAttach1 + ' \
        -H "Content-Type: application/json" \
        --user \'' + jirauser + ':' + jiraapitok + '\' \
        -F "file=@/jenkins/jenkinsECP/output/' + apnd + '/' + params.SQ_Project + '-' + env.BUILD_NUMBER + '.zip"'
}

/*
-> In this code, the multi-line shell command is enclosed in single-quotes ('), which prevents string interpolation. 
-> The variables are concatenated using the + operator, and the quotes within the command are escaped with a backslash (\).
-> (This code uses && instead of a backslash to continue the command on the next line

because it avoids using Groovy String interpolation to construct the curl command.
Instead, it uses concatenation to build the command string.

-> In the sh command, single quotes (') are used to delimit the command string, which prevents Groovy String interpolation from being performed. 
-> The variables are then concatenated to the command string using the + operator.
*/