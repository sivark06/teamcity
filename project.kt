package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

object Listuser3 : BuildType({
    name = "listuser3"

    artifactRules = "/"
    publishArtifacts = PublishMode.SUCCESSFUL

    params {
        password("env.CLIENT_SECRET", "******", display = ParameterDisplay.HIDDEN, readOnly = true)
        password("env.CLIENT_ID", "******", display = ParameterDisplay.HIDDEN, readOnly = true)
    }

    steps {
        script {
            scriptContent = """
                #!/bin/bash
                
                CLIENT_ID=%env.CLIENT_ID%
                CLIENT_SECRET=%env.CLIENT_SECRET%
                
                TOKEN_ENDPOINT=https://login.microsoftonline.com/e12daa31-a171-4a62-89f6-4e78cd972d5f/oauth2/token
                GROUP_ID=0ee09ed3-d31d-41d1-bd59-87f284948cd3
                
                # Get access token
                RESPONSE=${'$'}(curl -s --request POST -H  "Content-Type: application/x-www-form-urlencoded" https://login.microsoftonline.com/e12daa31-a171-4a62-89f6-4e78cd972d5f/oauth2/token --data "grant_type=client_credentials&client_id=${'$'}CLIENT_ID&client_secret=${'$'}CLIENT_SECRET"  --data-urlencode 'resource=https://graph.microsoft.com')
                ACCESS_TOKEN=${'$'}(echo "${'$'}RESPONSE" | sed "s/{.*\"access_token\":\"\([^\"]*\).*}/\1/g")
                echo "${'$'}ACCESS_TOKEN"
                
                # List group members
                MEMBERS_RESPONSE=${'$'}(curl -H  "Authorization: Bearer ${'$'}ACCESS_TOKEN" https://graph.microsoft.com/v1.0/groups/${'$'}GROUP_ID/members)
                #MEMBERS_RESPONSE=${'$'}(curl -H "Authorization: Bearer ${'$'}ACCESS_TOKEN" "https://graph.microsoft.com/v1.0/groups/${'$'}GROUP_ID/members?${'$'}select=id" | jq -r '.[] | .id')
                #MEMBERS_RESPONSE=${'$'}(curl --location --request GET 'https:/graph.microsoft.com/v1.0/groups/${'$'}GROUP_ID/members/' \
                #--header "Authorization: Bearer ${'$'}ACCESS_TOKEN")
                
                #echo "${'$'}MEMBERS_RESPONSE"
                # Extract email addresses
                EMAILS=${'$'}(echo "${'$'}MEMBERS_RESPONSE" |  grep -oP '"displayName":"\K[^"]+')
                
                # Print email addresses
                echo "LIST OF USERS"
                
                echo "${'$'}EMAILS"
                touch list.txt
                echo "${'$'}EMAILS" < list.txt
                mv list.txt .teamcity
            """.trimIndent()
        }
    }
})
