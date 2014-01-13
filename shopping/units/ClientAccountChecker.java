package shopping.units;

import shopping.objects.Amount;
import shopping.objects.Client;
import shopping.objects.Discount;

public class ClientAccountChecker {

	public static ClientAccountChecker checkAccount(
			Amount totalOrderAmount, Client client, Discount discount) 
	{
		ClientAccountChecker checker = new ClientAccountChecker(
				totalOrderAmount, client, discount);
		checker.checkAccount();
		return checker;
	}

	private final Amount totalOrderAmount;
	private final Client client;
	private final Discount discount;
	
	private boolean hasDiscount;
	private boolean premium;
	private boolean valid;

	private ClientAccountChecker(Amount totalOrderAmount, Client client,
			Discount discount) {
		this.totalOrderAmount = totalOrderAmount;
		this.client = client;
		this.discount = discount;
	}

	public void checkAccount() {
		// do some stuff here
		// {.....}
		hasDiscount = true;
		// {.....}
		premium = true;
		// {.....}
		// 
		valid = true;
	}

	public boolean shallChargeShippingCosts() 
	{
		return (!premium || totalOrderAmount.getValue() < 100) && !hasDiscount;
	}
	
	public boolean isAccountValid() 
	{
		return valid;
	}

}
