package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery,
	// and sizes and
	// crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "small";
	public final static String size_m = "medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";

	/**
	 * This function will handle the connection to the database
	 * 
	 * @return true if the connection was successfully made
	 * @throws SQLException
	 * @throws IOException
	 */
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	/**
	 *
	 * @param o order that needs to be saved to the database
	 * @throws SQLException
	 * @throws IOException
	 * @requires o is not NULL. o's ID is -1, as it has not been assigned yet. The
	 *           pizzas do not exist in the database yet, and the topping inventory
	 *           will allow for these pizzas to be made
	 * @ensures o will be assigned an id and added to the database, along with all
	 *          of it's pizzas. Inventory levels will be updated appropriately
	 */
	public static void addOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just adding the
		 * order to the order DB table, but we're also recording the necessary data for
		 * the delivery, dinein, and pickup tables
		 */
		
		//only add in order_ID and customer_ID
		String newOrder = "INSERT INTO cust_order(ORDER_ID, CUSTOMER_ID, ORDER_TYPE, TOTAL_PRICE, TOTAL_COST, TIME_STAMP, IS_COMPLETE)"
				+ " VALUES" + "(?,?,?,?,?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(newOrder)) {
			ps.setInt(1, o.getOrderID());
			ps.setInt(2, o.getCustID());
			ps.setString(3, o.getOrderType());
			ps.setDouble(4, o.getCustPrice());
			ps.setDouble(5, o.getBusPrice());
			ps.setString(6, o.getDate());
			ps.setInt(7, o.getIsComplete());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		
	}
	public static void updateOrder(Order o) throws SQLException, IOException {
		//update tablename set 
		connect_to_db();
		String updateOrder = "UPDATE cust_order SET ORDER_TYPE = ?, TOTAL_PRICE = ?" +
							", TOTAL_COST = ?" + ", TIME_STAMP = ?" 
					+ ", IS_COMPLETE = ? WHERE ORDER_ID = ?";
		try(PreparedStatement ps = conn.prepareStatement(updateOrder)){
			ps.setString(1, o.getOrderType());
			ps.setDouble(2, o.getCustPrice());
			ps.setDouble(3, o.getBusPrice());
			ps.setString(4, o.getDate());
			ps.setLong(5,  o.getIsComplete());
			ps.setInt(6, o.getOrderID());
			ps.executeUpdate();
			ps.close();
		}
		
		
		conn.close();
		
	}
	public static void addSubOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		if (o instanceof DineinOrder) {
			//DineinOrder dineinOrder = new DineinOrder(o.getOrderID(),o.getCustID(),
					//o.getDate(), o.getCustPrice(), o.getBusPrice(), o.getIsComplete(), ((DineinOrder) o).getTableNum());
			DineinOrder dineinOrder = (DineinOrder) o;
			String addOrder = "INSERT INTO dinein(ORDER_ID, TABLE_NUM)" + "VALUES" + "(?,?)";
			try (PreparedStatement ps2 = conn.prepareStatement(addOrder)) {
				ps2.setInt(1, dineinOrder.getOrderID());
				ps2.setInt(2, dineinOrder.getTableNum());
				ps2.executeUpdate();
				ps2.close();
			}

			catch (SQLException e) {
				System.out.println(e);
			}
		}
		if (o instanceof PickupOrder) {
			PickupOrder porder = (PickupOrder) o;
			String addPOrder = "INSERT INTO pickup(ORDER_ID)" + "VALUES" + "(?)";
			try (PreparedStatement ps2 = conn.prepareStatement(addPOrder)) {
				ps2.setInt(1, porder.getOrderID());
				ps2.executeUpdate();
				ps2.close();
			}

			catch (SQLException e) {
				System.out.println(e);
			}
		}
		if (o instanceof DeliveryOrder) {
			DeliveryOrder dorder = (DeliveryOrder) o;
			String addDOrder = "INSERT INTO delivery(ORDER_ID, ADDRESS)" + "VALUES" + "(?, ?)";
			try (PreparedStatement ps2 = conn.prepareStatement(addDOrder)) {
				ps2.setInt(1, dorder.getOrderID());
				ps2.setString(2, dorder.getAddress());
				ps2.executeUpdate();
				ps2.close();
			}

			catch (SQLException e) {
				System.out.println(e);
			}

			// DO NOT FORGET TO CLOSE YOUR CONNECTION
		}
		conn.close();
		
	}

	public static void addPizza(Pizza p) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into into the database. Keep in mind
		 * adding pizza discounts to that bridge table and instance of topping usage to
		 * that bridge table if you have't accounted for that somewhere else.
		 */

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static int getMaxPizzaID() throws SQLException, IOException {
		connect_to_db();
		/*
		 * A function I needed because I forgot to make my pizzas auto increment in my
		 * DB. It goes and fetches the largest PizzaID in the pizza table. You wont need
		 * this function if you didn't forget to do that
		 */
		int maxID = 0;
		ResultSet rs = null;
		String findMaxID = "Select IFNULL(MAX(PIZZA_ID),0) MAX_ID FROM pizza;";
		try (PreparedStatement ps = conn.prepareStatement(findMaxID)) {
			rs = ps.executeQuery();
			if (rs.next()) {
				maxID = rs.getInt("MAX_ID");
			}

			ps.close();
		} catch (SQLException e) {
			System.out.println(e);
		}

		conn.close();
		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		return maxID;
	}

	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException{
		connect_to_db();
		/*
		 * This function should 2 two things. We need to update the topping inventory
		 * every time we use t topping (accounting for extra toppings as well) and we
		 * need to add that instance of topping usage to the pizza-topping bridge if we
		 * haven't done that elsewhere Ideally, you should't let toppings go negative.
		 * If someone tries to use toppings that you don't have, just print that you've
		 * run out of that topping.
		 */

		// calculate toppings used
		int tID = t.getTopID();
		int extra = 0;
		String size = p.getSize();
		double howMany = 0;
		System.out.println("size " + size);
		switch (size) {
		case size_s:
			howMany = t.getPerAMT();
			break;
		case size_m:
			howMany = t.getMedAMT();
			break;
		case size_l:
			howMany = t.getLgAMT();
			break;
		case size_xl:
			howMany = t.getXLAMT();
		}
		System.out.println("calling from useTopping " + howMany);
		if (isDoubled) {
			extra = 1;
			howMany *= 2;
		}
		// decrease topping in topping table
		String decreaseTopping = "UPDATE topping SET CURR_INVENTORY " + "= CURR_INVENTORY -" + howMany
				+ " WHERE TOPPING_ID =" + tID;

		// connect to dbms
		try (PreparedStatement ps = conn.prepareStatement(decreaseTopping)) {
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			System.out.println(e);
		}

		// update topping bridge table
		String updateBridge = "INSERT INTO topping_selection (TOPPING_ID,PIZZA_ID,EXTRA)"
				+ "VALUES(?,?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(updateBridge)) {
			ps.setInt(1, t.getTopID());
			ps.setInt(2, p.getPizzaID());
			ps.setInt(3, extra);
		}

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. You might
		 * use this, you might not depending on where / how to want to update this table
		 */

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. You might
		 * use this, you might not depending on where / how to want to update this table
		 */

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This should add a customer to the database
		 */
		String makeCustomer = "INSERT INTO customer(CUSTOMER_ID,LAST_NAME,FIRST_NAME,PHONE)" + "VALUES" + "(?,?,?,?)";
		try (PreparedStatement ps = conn.prepareStatement(makeCustomer)) {
			ps.setInt(1, c.getCustID());
			ps.setString(2, c.getLName());
			ps.setString(3, c.getFName());
			ps.setString(4, c.getPhone());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
	}

	public static void CompleteOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to mark an order as complete in the DB. You may have a boolean field
		 * for this, or maybe a completed time timestamp. However you have it.
		 */

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void AddToInventory(Topping t, double toAdd) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Adds toAdd amount of topping to topping t.
		 */

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void printInventory() throws SQLException, IOException {
		connect_to_db();

		/*
		 * I used this function to PRINT (not return) the inventory list. When you print
		 * the inventory (either here or somewhere else) be sure that you print it in a
		 * way that is readable.
		 *
		 *
		 *
		 * The topping list should also print in alphabetical order
		 */

		ResultSet rs = null;
		String getInventory = "Select TOPPING_ID,TOPPING_NAME, CURR_INVENTORY from topping";
		System.out.println("ID\tTopping\tCurrInv");
		try (PreparedStatement ps = conn.prepareStatement(getInventory)) {
			rs = ps.executeQuery();

			while (rs.next()) {
				int toppingID = rs.getInt("TOPPING_ID");
				String toppingName = rs.getString("TOPPING_NAME");
				int currInv = rs.getInt("CURR_INVENTORY");
				System.out.println(toppingID + "\t" + toppingName + "\t" + currInv);
			}
		}
		conn.close();

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static ArrayList<Topping> getInventory() throws SQLException, IOException {
		connect_to_db();
		/*
		 * This function actually returns the toppings. The toppings should be returned
		 * in alphabetical order if you don't plan on using a printInventory function
		 */
		ArrayList<Topping> toppingList = new ArrayList<Topping>();
		ResultSet rs = null;
		String getInventory = "Select TOPPING_ID,TOPPING_NAME,PRICE,COST, MIN_INVENTORY," + " CURR_INVENTORY,"
				+ "NUM_PER_S,NUM_PER_M,NUM_PER_L,NUM_PER_XL from topping";
		try (PreparedStatement ps = conn.prepareStatement(getInventory)) {
			rs = ps.executeQuery();

			while (rs.next()) {
				int toppingID = rs.getInt("TOPPING_ID");
				String toppingName = rs.getString("TOPPING_NAME");
				int currInv = rs.getInt("CURR_INVENTORY");
				int minInv = rs.getInt("MIN_INVENTORY");
				double price = rs.getDouble("PRICE");
				double cost = rs.getDouble("COST");
				int numPerS = rs.getInt("NUM_PER_S");
				int numPerM = rs.getInt("NUM_PER_M");
				int numPerL = rs.getInt("NUM_PER_L");
				int numPerXL = rs.getInt("NUM_PER_XL");
				Topping t = new Topping(toppingID, toppingName, numPerS, numPerM, numPerL, numPerXL, price, cost,
						minInv, currInv);
				toppingList.add(t);
			}
		}
		conn.close();

		for (Topping t : toppingList) {
			System.out.println(t.toString());
		}

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		return toppingList;
	}

	public static ArrayList<Order> getCurrentOrders() throws SQLException, IOException {
		connect_to_db();
		/*
		 * This function should return an arraylist of all of the orders. Remember that
		 * in Java, we account for supertypes and subtypes which means that when we
		 * create an arrayList of orders, that really means we have an arrayList of
		 * dineinOrders, deliveryOrders, and pickupOrders.
		 *
		 * Also, like toppings, whenever we print out the orders using menu function 4
		 * and 5 these orders should print in order from newest to oldest.
		 */

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;
	}

	public static ArrayList<Order> sortOrders(ArrayList<Order> list) {
		/*
		 * This was a function that I used to sort my arraylist based on date. You may
		 * or may not need this function depending on how you fetch your orders from the
		 * DB in the getCurrentOrders function.
		 */

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;

	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder) {
		// Helper function I used to help sort my dates. You likely wont need these

		return false;
	}

	/*
	 * The next 3 private functions help get the individual components of a SQL
	 * datetime object. You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0, 4));
	}

	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}

	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base price (for the customer) for that size and crust
		// pizza Depending on how
		// you store size & crust in your database, you may have to do a conversion

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}

	public static String getCustomerName(int CustID) throws SQLException, IOException {
		/*
		 * This is a helper function I used to fetch the name of a customer based on a
		 * customer ID. It actually gets called in the Order class so I'll keep the
		 * implementation here. You're welcome to change how the order print statements
		 * work so that you don't need this function.
		 */
		connect_to_db();
		String ret = "";
		String query = "Select FIRST_NAME, LAST_NAME, PHONE From customer WHERE CUSTOMER_ID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);

		while (rset.next()) {
			ret = rset.getString(1) + " " + rset.getString(2);
		}
		conn.close();
		return ret;
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base cost (for the business) for that size and crust
		// pizza Depending on how
		// you store size and crust in your database, you may have to do a conversion

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}

	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		ArrayList<Discount> discs = new ArrayList<Discount>();
		connect_to_db();
		// returns a list of all the discounts.

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		return discs;
	}

	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		ArrayList<Customer> custs = new ArrayList<Customer>();
		connect_to_db();
		/*
		 * return an arrayList of all the customers. These customers should print in
		 * alphabetical order, so account for that as you see fit.
		 */
		ResultSet rs = null;
		String viewCustomers = "SELECT * FROM customer";
		try (PreparedStatement ps = conn.prepareStatement(viewCustomers)) {
			rs = ps.executeQuery();
			// System.out.println("Customer_ID\tFirstName\tLastName\tPhone Number");

			while (rs.next()) {
				int customer_id = rs.getInt("CUSTOMER_ID");
				String Fname = rs.getString("FIRST_NAME");
				String Lname = rs.getString("LAST_NAME");
				long phone = rs.getLong("phone");
				String ph = String.valueOf(phone);
//				System.out.println("\t" + customer_id + "\t\t" + Fname + "\t\t" + Lname
//						+ "\t\t" + phone);
				Customer c = new Customer(customer_id, Fname, Lname, ph);
				custs.add(c);
			}
		} catch (SQLException e) {
			System.out.println(e);
		}

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		conn.close();
		return custs;
	}

	public static int getNextOrderID() throws SQLException, IOException {
		/*
		 * A helper function I had to use because I forgot to make my OrderID auto
		 * increment...You can remove it if you did not forget to auto increment your
		 * orderID.
		 */
		connect_to_db();
		int maxID = 0;
		ResultSet rs = null;
		String findMaxID = "Select IFNULL(MAX(ORDER_ID),0) MAX_ID FROM cust_order;";
		try (PreparedStatement ps = conn.prepareStatement(findMaxID)) {
			rs = ps.executeQuery();
			if (rs.next()) {
				maxID = rs.getInt("MAX_ID");
			}

			ps.close();
		} catch (SQLException e) {
			System.out.println(e);
		}

		conn.close();
		

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
		return maxID;
	}
	public static int getNextCustomerID() throws SQLException, IOException{
		connect_to_db();
		int maxID = 0;
		ResultSet rs = null;
		String findMaxID = "Select IFNULL(MAX(CUSTOMER_ID),0) MAX_ID FROM customer;";
		try (PreparedStatement ps = conn.prepareStatement(findMaxID)) {
			rs = ps.executeQuery();
			if (rs.next()) {
				maxID = rs.getInt("MAX_ID");
			}

			ps.close();
		} catch (SQLException e) {
			System.out.println(e);
		}

		conn.close();
		return maxID;
	}

	public static void printToppingPopReport() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that these views need to exist in
		 * your DB, so be sure you've run your createViews.sql files on your testing DB
		 * if you haven't already.
		 *
		 * I'm not picky about how they print (other than that it should be in
		 * alphabetical order by name), just make sure it's readable.
		 */

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void printProfitByPizzaReport() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ProfitByPizza view. Remember that these views need to exist in
		 * your DB, so be sure you've run your createViews.sql files on your testing DB
		 * if you haven't already.
		 *
		 * I'm not picky about how they print, just make sure it's readable.
		 */

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void printProfitByOrderType() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that these views need to exist in
		 * your DB, so be sure you've run your createViews.sql files on your testing DB
		 * if you haven't already.
		 *
		 * I'm not picky about how they print, just make sure it's readable.
		 */

		// DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

}