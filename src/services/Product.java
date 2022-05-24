package services;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "product")
public class Product implements Serializable{
	
	  private static final long serialVersionUID = 1L;
	
	  private String ID = "\u003CID\u003E not found" ;
	  private String name = "";
	  private double price = 0.0;


	public Product() {}
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		this.ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		return "Product [ID=" + ID + ", name=" + name + ", price=" + price + "]";
	}


}
