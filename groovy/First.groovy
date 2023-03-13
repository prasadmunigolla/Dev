import groovy.json.JsonSlurperClassic
import groovy.json.JsonOutput
import java.lang.*
import hudson.model.*
import java.net.URL
import org.apache.commons.io.FileUtils
import java.lang.String
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;

def call(body) {
        //evaluate the body block, and collect configuration into the object
        def params = [:]
        def env_flag = false
        body.resolveStrategy = Closure.DELEGATE_FIRST
        body.delegate = params
        body()
        node(params.pipe_node) {
            stage('Validate Users') {
                echo "Trigerred environment: " + params.envnames1
                echo "username from main job: " + params.username1

                String envnames = params.envnames1
                String username = params.username1

                Map < String, ArrayList < String >> validateUserEnvs = [:]
                try {
                    ArrayList <String > file_list = new File('jenkins/jenkinsECP/input/file.txt').text.readLines();
                    println "filelist : ${file_list}"
                    for (int i=0; i<file_list.size(); i++){
                        println "Line (Details) from he config file: ${file_list[1]}"
                        def array = file_list[i].spilt('=')
                        validateUserEnvs.put(array[0], Arrays.asList(array[1].split(',')))
                    }
                println "Configured Users and VM's in the config file: ${validateUserEnvs}"
                if (validateUserEnvs != null && envnames != null) {
                    if (validateUserEnvs.containsKey(username)) {
                        println "Build User (${username}) is Present in Configured file"
                        ArrayList configEnvs = validateUserEnvs.get(username)
                        ArrayList selectedEnvs = Arrays.asList(envnames.split(';'))
                        println "Configured VM's from the File for the build user : ${configEnvs}"
                        println "Selected VM's from the ZVMS for the build user: ${selectedEnvs}"
                        int selectedEnvsCount = 0
                        for (int i = 0; i < selectedEnvs.size(); i++) {
                            if (configEnvs.contains(selectedEnvs[i]) {
                                    selectedEnvsCount++
                            }
                        }
                        println "No of seleced Envs have access :" + selectedEnvsCount
                        println "Total no of Envs :" + selectedEnvsCount
                        if (selectedEnvs.size() == selectedEnvsCount){
                            env_flag = true
                        } 
                    }
                } else {
                    println 'User/VMS Configuration may not configured'
                }
            println "User have build/Deployment access for selected Environments :"+env_flag
            if(!env_flag){
                println"Terminating the build process.."
                currentBuild.result = 'FAILURE'
                error('user dont have access to run the ZVMs')
            }
                } catch (NullPointerException e){
                    error('user dont have access to run the ZVMs')
                } catch ( Exception e){
                    error('user dont have access to run the ZVMs')
                }
            }
        }
}
