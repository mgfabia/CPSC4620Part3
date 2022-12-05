/*Mark Fabian, Mackenzie Blue*/
package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the functionality of each of these menu options' respective functions.
 * 
 * This file should need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove functions as you see necessary. But you MUST have all 8 menu functions (9 including exit)
 * 
 * Simply removing menu functions because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 * 
 */

public class Menu {
	public static void main(String[] args) throws SQLException, IOException, ParseException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Welcome to Mark and Mackenzie's Pizzeria!");
		
		int menu_option = 0;

		// present a menu of options and take their selection
		PrintMenu();
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
			case 1:// enter order
				EnterOrder();
				break;
			case 2:// view customers
				viewCustomers();
				break;
			case 3:// enter customer
				EnterCustomer();
				break;
			case 4:// view order
				// open/closed/date
				ViewOrders();
				break;
			case 5:// mark order as complete
				MarkOrderAsComplete();
				break;
			case 6:// view inventory levels
				ViewInventoryLevels();
				break;
			case 7:// add to inventory
				AddInventory();
				break;
			case 8:// view reports
				PrintReports();
				break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}


	}

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException 
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		/*
		 * EnterOrder should do the following:
		 * Ask if the order is for an existing customer -> If yes, select the customer. If no -> create the customer (as if the menu option 2 was selected).
		 * 
		 * Ask if the order is delivery, pickup, or dinein (ask for orderType specific information when needed)
		 * 
		 * Build the pizza (there's a function for this)
		 * 
		 * ask if more pizzas should be be created. if yes, go back to building your pizza. 
		 * 
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * apply the pizza to the order (including to the DB)
		 * 
		 * return to menu
		 */
		Order order = new Order(0, 0, "dinein", "n ", 0, 0, 0);
		int orderId = DBNinja.getNextOrderID() + 1;
		System.out.println(orderId);
		order.setOrderID(orderId);
		System.out.println("Is this order for an existing customer? Answer y/n: ");
		
		
		
		
		String existing = reader.readLine();	
		if(!existing.equalsIgnoreCase("y") || !existing.equalsIgnoreCase("n")) {
			System.out.println("Please only enter y or n");
			existing = reader.readLine();
		}
		

		if (existing.equals("y")){
			System.out.println("Here is a list of the current customers");
			ArrayList<Customer> custList = DBNinja.getCustomerList();
			for (Customer cust: custList){
				System.out.println(cust);
			}
			System.out.println("Which customer is this order for? Enter ID Number ");
			int custID = Integer.parseInt(reader.readLine());
			order.setCustID(custID);
			DBNinja.addOrder(order);
			System.out.println("Is this order for: \n1.)Dine-in\n2.)Pick-up\n3.)Delivery\nEnter to corresponding number\n ");
			int orderType = Integer.parseInt(reader.readLine());
			if (orderType == 1){
				System.out.println("Enter the table number");
				int tableNum = Integer.parseInt(reader.readLine());
				
				
				Pizza p = buildPizza(orderId);
				
				order.setDate(p.getPizzaDate());
				order.setBusPrice(p.getBusPrice());
				order.setCustPrice(p.getCustPrice());
				order.setIsComplete(0);
				order.setOrderType(DBNinja.dine_in);
				DBNinja.updateOrder(order);
				DBNinja.addPizza(p);
				//create dinein type order and addd tablenum
				DineinOrder dineinOrder = new DineinOrder(order.getOrderID(),order.getCustID(),
						order.getDate(), order.getCustPrice(), order.getBusPrice(), order.getIsComplete(), tableNum);
				DBNinja.addSubOrder(dineinOrder);

			}
			if (orderType == 2){
				int isPickedUp = 0;
				Pizza p = buildPizza(orderId);
				order.setDate(p.getPizzaDate());
				order.setBusPrice(p.getBusPrice());
				order.setCustPrice(p.getCustPrice());
				order.setIsComplete(0);
				order.setOrderType(DBNinja.pickup);
				DBNinja.updateOrder(order);
				DBNinja.addPizza(p);
				PickupOrder pickup = new PickupOrder(order.getOrderID(),order.getCustID(),order.getDate(),order.getCustPrice(),
						order.getBusPrice(),isPickedUp,order.getIsComplete());
						
				DBNinja.addSubOrder(pickup);
			}
			if (orderType == 3){
				System.out.println("Enter the customer address");
				String addy = reader.readLine();
				Pizza p = buildPizza(orderId);
				order.setDate(p.getPizzaDate());
				order.setBusPrice(p.getBusPrice());
				order.setCustPrice(p.getCustPrice());
				order.setIsComplete(0);
				order.setOrderType(DBNinja.delivery);
				DBNinja.updateOrder(order);
				DBNinja.addPizza(p);
				DeliveryOrder deliv = new DeliveryOrder(order.getOrderID(), order.getCustID(), order.getDate(), order.getCustPrice(), order.getBusPrice()
						, order.getIsComplete(), addy);
				DBNinja.addSubOrder(deliv);
				
			}


		}
		else{
			EnterCustomer();
			order.setCustID(DBNinja.getNextCustomerID());
			DBNinja.addOrder(order);
			
			System.out.println("Is this order for: \n1.)Dine-in\n2.)Pick-up\n3.)Delivery\nEnter to corresponding number\n ");
			int orderType = Integer.parseInt(reader.readLine());
			
			if (orderType == 1){
				System.out.println("Enter the table number");
				int tableNum = Integer.parseInt(reader.readLine());
				
				//populate order with correct values
				Pizza p = buildPizza(orderId);
			
				order.setDate(p.getPizzaDate());
				order.setBusPrice(p.getBusPrice());
				order.setCustPrice(p.getCustPrice());
				order.setIsComplete(0);
				order.setOrderType(DBNinja.dine_in);
				DBNinja.updateOrder(order);
				DBNinja.addPizza(p);
				//create dinein type order and addd tablenum
				DineinOrder dineinOrder = new DineinOrder(order.getOrderID(),order.getCustID(),
						order.getDate(), order.getCustPrice(), order.getBusPrice(), order.getIsComplete(), tableNum);
				DBNinja.addSubOrder(dineinOrder);
			}
			if (orderType == 2){
				int isPickedUp = 0;
				Pizza p = buildPizza(orderId);
				order.setDate(p.getPizzaDate());
				order.setBusPrice(p.getBusPrice());
				order.setCustPrice(p.getCustPrice());
				order.setIsComplete(0);
				order.setOrderType(DBNinja.pickup);
				DBNinja.updateOrder(order);
				DBNinja.addPizza(p);
				PickupOrder pickup = new PickupOrder(order.getOrderID(),order.getCustID(),order.getDate(),order.getCustPrice(),
						order.getBusPrice(),isPickedUp,order.getIsComplete());
						
				DBNinja.addSubOrder(pickup);
			}
			if (orderType == 3){
				System.out.println("What address would you like to enter?");
				String address = reader.readLine();
				Pizza p = buildPizza(orderId);
				order.setDate(p.getPizzaDate());
				order.setBusPrice(p.getBusPrice());
				order.setCustPrice(p.getCustPrice());
				order.setIsComplete(0);
				order.setOrderType(DBNinja.delivery);
				DBNinja.updateOrder(order);
				DBNinja.addPizza(p);
				DeliveryOrder deliv = new DeliveryOrder(order.getOrderID(), order.getCustID(), order.getDate(), order.getCustPrice(), order.getBusPrice()
						, order.getIsComplete(), address);
				DBNinja.addSubOrder(deliv);
			}
		}
		System.out.println("Finished adding order...Returning to menu...");
	}


	public static void viewCustomers() throws SQLException, IOException
	{
		/*
		 * Simply print out all of the customers from the database.
		 *
		 */
		ArrayList<Customer> customers = DBNinja.getCustomerList();

		for (Customer c: customers) {
			System.out.println(c.toString());
		}

	}


	// Enter a new customer in the database
	public static void EnterCustomer() throws SQLException, IOException
	{
		/*
		 * Ask what the name of the customer is. YOU MUST TELL ME (the grader) HOW TO FORMAT THE FIRST NAME, LAST NAME, AND PHONE NUMBER.
		 * If you ask for first and last name one at a time, tell me to insert First name <enter> Last Name (or separate them by different print statements)
		 * If you want them in the same line, tell me (First Name <space> Last Name).
		 *
		 * same with phone number. If there's hyphens, tell me XXX-XXX-XXXX. For spaces, XXX XXX XXXX. For nothing XXXXXXXXXXXX.
		 *
		 * I don't care what the format is as long as you tell me what it is, but if I have to guess what your input is I will not be a happy grader
		 *
		 * Once you get the name and phone number (and anything else your design might have) add it to the DB
		 */
		
		int customerID = DBNinja.getNextCustomerID() + 1;
		String Fname = "";
		String Lname = "";
		String phone = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the customer's first name");
		Fname = reader.readLine();
		System.out.println("Enter the customer's last name");
		Lname = reader.readLine();
		System.out.println("Enter the customer's phone number");
		phone = reader.readLine();

		
		Customer customer = new Customer(customerID,Fname,Lname,phone);
		DBNinja.addCustomer(customer);

		System.out.println(customer.toString());

	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException, ParseException
	{
		//this is wrong

		/*
		 * This should be subdivided into two options: print all orders (using simplified view) and print all orders (using simplified view) since a specific date.
		 *
		 * Once you print the orders (using either sub option) you should then ask which order I want to see in detail
		 *
		 * When I enter the order, print out all the information about that order, not just the simplified view.
		 *
		 */
		
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Would you like to:\n(a) display all orders\n(b) display orders since a specific date");
		String option = "";
		option = reader.readLine();
		ArrayList<Order> currOrders = DBNinja.getCurrentOrders();
		
		ArrayList<Order>sortedOrders = DBNinja.sortOrders(currOrders);
		if (option.equalsIgnoreCase("a")) {
			for (Order order : sortedOrders) {
				   System.out.println(order);
		
				}
		   System.out.println("Which order would you like to see in detail? Enter the number: ");
		   int whichOrder = Integer.parseInt(reader.readLine());
		   for (Order order : currOrders) {
		      if (order.getOrderID() == whichOrder && order.getOrderType().equalsIgnoreCase("dinein")) {
		         DineinOrder dinein = DBNinja.getDineinOrder(order);
		         System.out.println(dinein.toString());
		      }
		      if (order.getOrderID() == whichOrder && order.getOrderType().equalsIgnoreCase("pickup")) {
		         PickupOrder pickupOrder = DBNinja.getPickupOrder(order);
		         System.out.println(pickupOrder.toString());
		      }
		      if (order.getOrderID() == whichOrder && order.getOrderType().equalsIgnoreCase("delivery")) {
		         DeliveryOrder deliveryOrder = DBNinja.getDeliveryOrder(order);
		         System.out.println(deliveryOrder.toString());
		      }
		   }
		}else if (option.equalsIgnoreCase("b")) {

		         System.out.println("Which date would you like to restrict by? Enter with the following format");
		         System.out.println("Enter year as yyyy");
		         String year = reader.readLine();
		         System.out.println("Enter month as mm");
		         String month = reader.readLine();
		         System.out.println("Enter day as dd");
		         String day = reader.readLine();
		         
		         String dateString = year + month  +  day;
		        
		         //only show after appropriate data
		         ArrayList<Order> restricted = DBNinja.restrictOrders(sortedOrders,dateString);
		         for (Order order : restricted) {
					   System.out.println(order);
			
					}
		         System.out.println("Which order would you like to see in detail? Enter the number: ");
		         int whichO = Integer.parseInt(reader.readLine());
		         for (Order orders : currOrders) {
		            if (orders.getOrderID() == whichO && orders.getOrderType().equalsIgnoreCase("dinein")) {
		               DineinOrder dinein = DBNinja.getDineinOrder(orders);
		               System.out.println(dinein.toString());
		            }
		            if (orders.getOrderID() == whichO && orders.getOrderType().equalsIgnoreCase("pickup")) {
		               PickupOrder pickupOrder = DBNinja.getPickupOrder(orders);
		               System.out.println(pickupOrder.toString());
		            }
		            if (orders.getOrderID() == whichO && orders.getOrderType().equalsIgnoreCase("delivery")) {
		               DeliveryOrder deliveryOrder = DBNinja.getDeliveryOrder(orders);
		               System.out.println(deliveryOrder.toString());
		            }
		         }

		      }
		
	}


	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException
	{
		/*All orders that are created through java (part 3, not the 7 orders from part 2) should start as incomplete
		 *
		 * When this function is called, you should print all of the orders marked as complete
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 *
		 *
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<Order> currOrders = DBNinja.getCurrentOrders();
		if(currOrders.isEmpty()){
		   System.out.println("There are currently no open orders...");
		}
		else {
		   for(Order order: currOrders){
		      System.out.println(order);
		   }

		   System.out.println("Which order would you like to mark complete? Enter the ID: ");
		   int orderID = Integer.parseInt(reader.readLine());
		   for (Order order : currOrders) {
		      if (order.getOrderID() == orderID) {
		         DBNinja.CompleteOrder(order);
		      }
		   }
		}
		
		







	}

	// See the list of inventory and it's current level
	public static void ViewInventoryLevels() throws SQLException, IOException
	{
		//print the inventory. I am really just concerned with the ID, the name, and the current inventory


		DBNinja.printInventory();




	}

	// Select an inventory item and add more to the inventory level to re-stock the
	// inventory
	public static void AddInventory() throws SQLException, IOException
	{
		/*
		 * This should print the current inventory and then ask the user which topping they want to add more to and how much to add
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		DBNinja.printInventory();
		System.out.println("Which topping would you like to add more to? Enter the ID:");
		int toppingID = Integer.parseInt(reader.readLine());
		System.out.println("How many more would you like to add?");
		int numtoAdd = Integer.parseInt(reader.readLine());
		ArrayList<Topping> currToppings = DBNinja.getInventory();
		Topping toppingToUpdate = new Topping(0,"",0,0,0,0,0,0,0,0);
		for (Topping t : currToppings){
		   if(t.getTopID() == toppingID){
		      toppingToUpdate = t;
		   }
		}
		DBNinja.AddToInventory(toppingToUpdate, numtoAdd);






	}

	// A function that builds a pizza. Used in our add new order function
	public static Pizza buildPizza(int orderID) throws SQLException, IOException
	{

		/*
		 * This is a helper function for first menu option.
		 *
		 * It should ask which size pizza the user wants and the crustType.
		 *
		 * Once the pizza is created, it should be added to the DB.
		 *
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 *
		 * We then need to add pizza discounts (again, to here and to the database)
		 *
		 * Once the discounts are added, we can return the pizza
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<Topping> toppingSelection = new ArrayList<Topping>();
		ArrayList<Topping> toppingList = DBNinja.getInventory();
		Pizza p = null;
		int size = 0;
		int crustType = 0;
		int topping = 0;
		String Stopping = " ";
		String Ssize = " ";
		String scrustType = " ";

		System.out.println("What size pizza?");
		System.out.println("1. Small\n2.Medium\n3. Large\n4. Extra Large\nEnter the corresponding number:");
		size = Integer.parseInt(reader.readLine());
		System.out.println("What type of crust do you want?");
		System.out.println("1. Thin\n2. Original\n3. Pan\n4. Gluten-Free\nEnter the corresponding number:");
		crustType = Integer.parseInt(reader.readLine());

		switch(size) {
			case 1: Ssize = DBNinja.size_s;
				break;
			case 2: Ssize = DBNinja.size_m;
				break;
			case 3: Ssize = DBNinja.size_l;
				break;
			case 4: Ssize = DBNinja.size_xl;
		}
		switch(crustType) {
			case 1: scrustType = DBNinja.crust_thin;
				break;
			case 2: scrustType = DBNinja.crust_orig;
				break;
			case 3: scrustType = DBNinja.crust_pan;
				break;
			case 4: scrustType = DBNinja.crust_gf;

		}
		//pizza object
		//find max pizzaID
		//find max
		int nextPizzaID = DBNinja.getMaxPizzaID() + 1;

		//find date
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		

		p = new Pizza(nextPizzaID, Ssize, scrustType, orderID,0,dtf.format(now),0,0);
		//getTopping List

		//toppings menu

		int topping_choice = 0;
		String topping_extra = " ";
		while(true) {
			System.out.println("Printing current toppings list:\n");
			DBNinja.printInventory();
			System.out.println("Which topping do you want to add? Enter the topping ID. Enter -1 to stop adding toppings.");
			topping_choice = Integer.parseInt(reader.readLine());
			if(topping_choice < 0) {
				break;
			}
			System.out.println("Do you want to add extra topping? Enter y/n");
			topping_extra = reader.readLine();




			//find topping from topping list by using topping_choice
			//update topping count in topping table and topping_selection table
			//add to toppingSelection list

			for(Topping t: toppingList) {
				if(topping_choice == t.getTopID()) {

					if(topping_extra.equals("y")) {
						DBNinja.useTopping(p, t, true);
						p.addToppings(t, true);

					}else {
						DBNinja.useTopping(p, t, false);
						p.addToppings(t, false);
					}
				}
			}
		}
		int choice = 0;
		String res = " ";
		System.out.println("Do you want to add discounts to this pizza? Enter y/n");
		res = reader.readLine();
		if(res.equalsIgnoreCase("y")) {
			ArrayList<Discount>discList = new ArrayList<Discount>();
			while(true) {
				//adding a discount 
				
				ArrayList<Discount> discs  = DBNinja.getDiscountList();
				for(Discount d: discs) {
					System.out.println(d.toString());
				}
				System.out.println("Which pizza discount would you like to apply? Enter the discount ID. Enter -1 to stop adding"
					+ "discounts.");
				choice = Integer.parseInt(reader.readLine());
				if(choice < 0) {
					break;
				}
				//apply pizza discount 
				for(Discount d: discs) {
					if(choice == d.getDiscountID()) {
						discList.add(d);
						DBNinja.usePizzaDiscount(p, d);
					}
				}
				
			}
		}
		//apply math
		
		
		
		

		System.out.println(p.toString());
		
		return p;
	}

	private static int getTopIndexFromList(int TopID, ArrayList<Topping> tops)
	{
		/*
		 * This is a helper function I used to get a topping index from a list of toppings
		 * It's very possible you never need a function like this
		 *
		 */
		int ret = -1;
		return ret;
	}


	public static void PrintReports() throws SQLException, NumberFormatException, IOException
	{
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		   System.out.println("Which report do you want to print? Enter\n1.)ToppingPopularity\n2.)ProfitByPizza\n3.)ProfitByOrderType\n");
		   int choice = Integer.parseInt(reader.readLine());
		   if(choice == 1){
		      DBNinja.printToppingPopReport();
		   }
		   else if(choice ==2){
		      DBNinja.printProfitByPizzaReport();
		   }
		   else if(choice == 3){
		      DBNinja.printProfitByOrderType();
		   }
			  
		   
		
		/*
		 * This function calls the DBNinja functions to print the three reports.
		 *
		 * You should ask the user which report to print
		 */
		
	}

}