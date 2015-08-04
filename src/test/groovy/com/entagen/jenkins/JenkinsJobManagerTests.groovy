package com.entagen.jenkins

import org.junit.Test

class JenkinsJobManagerTests extends GroovyTestCase {
    @Test public void testFindTemplateJobs() {
        JenkinsJobManager jenkinsJobManager = new JenkinsJobManager(templateJobPrefix: "myproj", templateBranchName: "master", jenkinsUrl: "http://dummy.com", gitUrl: "git@dummy.com:company/myproj.git")
        List<String> allJobNames = [
                "myproj-master",
                "otherproj-master",
                "myproj-featurebranch"
        ]
        List<TemplateJob> templateJobs = jenkinsJobManager.findRequiredTemplateJobs(allJobNames)
        assert templateJobs.size() == 1
        TemplateJob templateJob = templateJobs.first()
        assert templateJob.jobName == "myproj-master"
        assert templateJob.templateBranchName == "master"
    }


    @Test public void testFindTemplateJobs_noMatchingJobsThrowsException() {
        JenkinsJobManager jenkinsJobManager = new JenkinsJobManager(templateJobPrefix: "myproj", templateBranchName: "master", jenkinsUrl: "http://dummy.com", gitUrl: "git@dummy.com:company/myproj.git")
        List<String> allJobNames = [
                "otherproj-master",
                "myproj-foo-featurebranch"
        ]
        String result = shouldFail(AssertionError) {
            jenkinsJobManager.findRequiredTemplateJobs(allJobNames)
        }

        assert result == "Unable to find any jobs matching template regex: ^(myproj)-(master)\$\nYou need at least one job to match the templateJobPrefix and templateBranchName suffix arguments. Expression: (templateJobs?.size() > 0)"
    }



    @Test public void testTemplateJobSafeNames() {
        TemplateJob templateJob = new TemplateJob(jobName: "myproj-master", baseJobName: "myproj", templateBranchName: "master")

        assert "myproj-myfeature" == templateJob.jobNameForBranch("myfeature")
        assert "myproj-ted_myfeature" == templateJob.jobNameForBranch("ted/myfeature")
    }


    @Test public void testInitGitApi_noBranchRegex() {
        JenkinsJobManager jenkinsJobManager = new JenkinsJobManager(gitUrl: "git@dummy.com:company/myproj.git", jenkinsUrl: "http://dummy.com")
        assert jenkinsJobManager.gitApi
    }

    @Test public void testInitGitApi_withBranchRegex() {
        JenkinsJobManager jenkinsJobManager = new JenkinsJobManager(gitUrl: "git@dummy.com:company/myproj.git", branchNameRegex: 'feature\\/.+|release\\/.+|master', jenkinsUrl: "http://dummy.com")
        assert jenkinsJobManager.gitApi
    }
}
