
public class Date implements Comparable<Date> {

	private int year;
	private int month;
	private int day;
	private int hours;
	private int minutes;
	private int seconds;
	private int milliseconds;

	public Date(int year, int month, int day, int hours, int minutes, int seconds, int milliseconds) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.milliseconds = milliseconds;
	}

	@Override
	public String toString() {
		return "" + year + month + day + hours + minutes + seconds + milliseconds;
	}

	@Override
	public int compareTo(Date o) {
		if (year > o.year)
			return 1;
		else if (year < o.year)
			return -1;
		else if (year == o.year) {
			if (month > o.month)
				return 1;
			else if (month < o.month)
				return -1;
			else if (month == o.month) {
				if (day > o.day)
					return 1;
				else if (day < o.day)
					return -1;
				else if (day == o.day) {
					if (hours > o.hours)
						return 1;
					else if (hours < o.hours)
						return -1;
					else if (hours == o.hours) {
						if (minutes > o.minutes)
							return 1;
						else if (minutes < o.minutes)
							return -1;
						else if (minutes == o.minutes) {
							if (seconds > o.seconds)
								return 1;
							else if (seconds < o.seconds)
								return -1;
							else if (seconds == o.seconds) {
								if (milliseconds > o.milliseconds)
									return 1;
								else if (milliseconds < o.milliseconds)
									return -1;
								else if (milliseconds == o.milliseconds) {
									return 0;
								}
							}
						}
					}
				}
			}
		}
		return 0;
	}

}
