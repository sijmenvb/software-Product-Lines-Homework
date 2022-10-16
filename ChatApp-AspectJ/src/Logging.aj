
public aspect Logging {
	after(): call(void main.Main.main()) {
		System.out.print(" Logging");
	}
}
