package cards.bean;

import java.util.Objects;

public class HostDetails {
    private String name;
    private String address;

    public HostDetails(){}

    public HostDetails(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(address);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HostDetails && Objects.equals(this.address, ((HostDetails) obj).address);
    }
}
