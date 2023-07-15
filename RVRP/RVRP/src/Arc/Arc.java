package Arc;

public class Arc {
	
	public int index;
	public int source;
	public int depot;
	public double distance;
	public double addTime;
	
	public Arc(int index, int i, int j, double dis, double addTime) {
		this.index = index;
		this.source = i;
		this.depot = j;
		this.distance = dis;
		this.addTime = addTime;
	}
	
	public Arc(Arc original) {
		this.index = original.index;
		this.source = original.source;
		this.depot = original.depot;
		this.distance = original.distance;
		this.addTime = original.addTime;
	}
	
	public int getIndex() {
		return this.index;
	}
	public int getSource() {
		return this.source;
	}
	public int getDepot() {
		return this.depot;
	}
	public double getDistance() {
		return this.distance;
	}
	public double getAddTime() {
		return this.addTime;
	}
	public Arc returnObject() {
		return this;
	}
}
