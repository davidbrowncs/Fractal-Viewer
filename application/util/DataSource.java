package util;

public interface DataSource {
	public void addDataObserver(DataObserver observer);
	public void removeDataObserver(DataObserver observer);
}
