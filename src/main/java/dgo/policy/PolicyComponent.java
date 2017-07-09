package dgo.policy;

import dgo.model.Goban;
import dgo.model.GobanValuesD;

public interface PolicyComponent {
	public GobanValuesD evaluate(Goban gb);
}
