package sk.vub.demo.multipart.demorestdocmultipart;

public class MetadataDTO {

    private String name;

    public MetadataDTO(String name) {
        this.name = name;
    }

    public MetadataDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MetadataDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
