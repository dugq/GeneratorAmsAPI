package com.dugq.pojo.yapi;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * @author dugq
 * @date 2021/8/11 9:29 下午
 */
public class YapiSearchResult {
    private List<Group> group;
    private List<YapiProject> project;
    @JSONField(name = "interface")
    private List<Api> api;

    public List<Group> getGroup() {
        return group;
    }

    public void setGroup(List<Group> group) {
        this.group = group;
    }

    public List<YapiProject> getProject() {
        return project;
    }

    public void setProject(List<YapiProject> project) {
        this.project = project;
    }

    public List<Api> getApi() {
        return api;
    }

    public void setApi(List<Api> api) {
        this.api = api;
    }

    public class YapiProject {
        private String name;
        private long groupId;
        @JSONField(name = "_id")
        private long id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getGroupId() {
            return groupId;
        }

        public void setGroupId(long groupId) {
            this.groupId = groupId;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public class Group{
        private String groupName;
        @JSONField(name = "_id")
        private long id;

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }


    public class Api{
        private long projectId;
        private String title;
        @JSONField(name = "_id")
        private long id;

        public long getProjectId() {
            return projectId;
        }

        public void setProjectId(long projectId) {
            this.projectId = projectId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

}
