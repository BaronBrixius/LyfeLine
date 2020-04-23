import java.util.Calendar;

public class Date implements Comparable<Date> {

	private int year = 1;
	private int month = 1;
	private int day = 1;
	private int hours;
	private int minutes;
	private int seconds;
	private int milliseconds;

	public Date(){}
	
	public Date(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public int getMilliseconds() {
		return milliseconds;
	}

	public void setMilliseconds(int milliseconds) {
		this.milliseconds = milliseconds;
	}


}
