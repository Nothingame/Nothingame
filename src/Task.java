class Task {

    private String name, status;

    public void change_status() {
        if (status.equals(Main.comm_desc(Main.Commands.status0))) {
            this.status = Main.comm_desc(Main.Commands.status1);
        } else {
            this.status = Main.comm_desc(Main.Commands.status0);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}