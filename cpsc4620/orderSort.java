


package cpsc4620;

import java.util.Comparator;

public class orderSort implements Comparator<Order> {


@Override
public int compare(Order o1, Order o2) {
	// TODO Auto-generated method stub
	long num1 = Long.parseLong(o1.getDate());
	long num2 = Long.parseLong(o2.getDate());
	return Long.compare(num1,num2);
	
}
}