package in.reqres.dto;

public class UserCreated extends User {
    private String id;
    private String createdAt;

    public UserCreated() {
    }

    public UserCreated(String name, String job, String id, String createdAt) {
        super(name, job);
        this.id = id;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getJob() {
        return super.getJob();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setJob(String job) {
        super.setJob(job);
    }
}
