package shopping.objects;

import java.util.List;
import java.util.Map;

public class ShippingSettings {

	public boolean shallNotifyClient() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean shallChargeShippingCosts() {
		// TODO Auto-generated method stub
		return false;
	}

	public ShippingParameter getShippingParameter() {
		// TODO Auto-generated method stub
		return null;
	}

	public ClientAccountParameter getClientAccountParameter() {
		// TODO Auto-generated method stub
		return null;
	}

	public static ShippingSettings loadSettings(Order order,
			List<Item> items, Discount controlType,
			ContactType contactType, Boolean chargeShippingCosts) {
		// TODO Auto-generated method stub
		return null;
	}

}
