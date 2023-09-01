package org.kie.dmn.backend.marshalling.v1_3.extensions;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ProjectCharter")
public class ProjectCharter {

    @XStreamAlias("projectGoals")
    private String projectGoals;

    @XStreamAlias("projectChallenges")
    private String projectChallenges;

    @XStreamAlias("projectStakeholders")
    private String projectStakeholders;

    public String getProjectGoals() {
        return projectGoals;
    }

    public void setProjectGoals(String content) {
        this.projectGoals = content;
    }

    public String getProjectChallenges() {
        return projectChallenges;
    }

    public void setProjectChallenges(String projectChallenges) {
        this.projectChallenges = projectChallenges;
    }

    public String getProjectStakeholders() {
        return projectStakeholders;
    }

    public void setProjectStakeholders(String projectStakeholders) {
        this.projectStakeholders = projectStakeholders;
    }

}
