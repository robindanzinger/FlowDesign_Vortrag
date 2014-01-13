package shopping;

import java.util.ArrayList;
import java.util.List;

import shopping.helper.ErrorHelper;
import shopping.objects.ClientAccountParameter;
import shopping.objects.ContactType;
import shopping.objects.Discount;
import shopping.objects.Item;
import shopping.objects.Order;
import shopping.objects.OrderHelper;
import shopping.objects.OrderResponse;
import shopping.objects.OrderValidStatus;
import shopping.objects.ShippingParameter;
import shopping.objects.ShippingSettings;
import shopping.units.BillCreatorWithShippingCosts;
import shopping.units.BillCreatorWithoutShippingCosts;
import shopping.units.ClientAccountChecker;
import shopping.units.OrderChecker;
import shopping.units.PaketPreparer;
import shopping.units.PaybackPointsUpdater;

public class StandardBuyFlow {

	private final Order order;
	private final ContactType contactType;
	private final Discount discount;
	private final Boolean chargeShippingCosts;
	private final List<Item> items;

	private OrderValidStatus orderValidStatus;
	private OrderResponse orderResponse;
	private ShippingParameter shippingParameter;
	private boolean shipped;
	private boolean isSelfCollectorClient;
	private boolean shallChargeShippingCosts;
	private ClientAccountParameter clientAccountParameter;

	public StandardBuyFlow(Order order,
			ArrayList<String> transactionLocalIds, ContactType contactType,
			Discount discount,
			Boolean chargeShippingCosts,
			List<Item> items) {
		this.order = order;
		this.contactType = contactType;
		this.discount = discount;
		this.chargeShippingCosts = chargeShippingCosts;
		this.items = items;
	}

	public void executeBuyFlow() {
		checkOrder();
		if (shallAbort()) {
			abort();
		} else {
			loadItems();
			checkItems();
			if (shallAbort()) {
				abort();
			} else {
				loadShippingParameter();
				if (clientIsSelfCollector()) {
					reserveItems();
				} else if (shallChargeShippingCosts()) {
					preparePaket();
					createBillWithShippingCosts();
					updatePaybackPoints();
				} else {
					preparePaket();
					createBillWithoutShippingCosts();
				}
			}
		}
	}
	
	private void checkOrder() {
		OrderChecker checker = OrderChecker.checkInput(order,
				contactType, discount);
		orderValidStatus = checker.getStatus();
	}
	
	private boolean shallAbort() {
		if (orderValidStatus != null) {
			return true;
		}
		return false;
	}
	
	private void abort() {
		ErrorHelper.cancel(orderResponse, orderValidStatus);
	}

	private void loadItems() {	
		for (Item item : items) {
			if (item.isAvalailable(item.getAmount())) {
				item.reserve(item.getAmount());
			}
		}
	}
	
	private void checkItems() {
		if (items == null || items.isEmpty()) {
			orderValidStatus = OrderHelper
					.createStatus("No items selected");
		}
	}
	
	private void loadShippingParameter() {
		ShippingSettings shippingSettings = ShippingSettings.loadSettings(
				order, items, discount, contactType, chargeShippingCosts);
		
		isSelfCollectorClient = shippingSettings.shallNotifyClient();
		shallChargeShippingCosts = shippingSettings.shallChargeShippingCosts();
		shippingParameter = shippingSettings.getShippingParameter();
		clientAccountParameter = shippingSettings.getClientAccountParameter();
	}
	
	private boolean clientIsSelfCollector() {
		return isSelfCollectorClient;
	}
	
	private void reserveItems() {
		// do nothing;
	}
	
	private boolean shallChargeShippingCosts() {
		return isClientChargeableForShippingCosts() && shallChargeShippingCosts;
	}

	private boolean isClientChargeableForShippingCosts() {
		ClientAccountChecker accountChecker = ClientAccountChecker.checkAccount(order.getTotalOrderAmount(), order.getClient(), discount);
		return !accountChecker.shallChargeShippingCosts();
	}
	
	private void preparePaket() {
		PaketPreparer.prepare(order, items,
				contactType, orderResponse, shippingParameter,
				shallChargeShippingCosts);
	}
	
	private void createBillWithShippingCosts() {
		BillCreatorWithShippingCosts
				.createBill(items, order, contactType,
						shippingParameter, clientAccountParameter);
	}
	
	private void updatePaybackPoints() {
		if (shipped) {
			new PaybackPointsUpdater(order)
			.updatePaybackPoints();
		}
	}

	private void createBillWithoutShippingCosts() {
		BillCreatorWithoutShippingCosts
				.createBill(items, order, contactType,
						shippingParameter, clientAccountParameter);
	}

}
