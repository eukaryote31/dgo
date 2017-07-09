package dgo.value;

public class SimpleExpValFactory implements ValueFactory {
	// has no state, so okay to use singleton. 
	private static final ValueComponent INSTANCE = new SimpleExpValue();
	
	@Override
	public ValueComponent build() {
		return INSTANCE;
	}

}
