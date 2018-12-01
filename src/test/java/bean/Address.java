package bean;

import java.io.Serializable;

/**
 * @author WuFei
 */
public class Address implements Serializable {

    private int postcode;//邮编


    public Address(int postcode) {
        this.postcode = postcode;
    }

    public Address() {
        System.out.println("hh");
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }
}
