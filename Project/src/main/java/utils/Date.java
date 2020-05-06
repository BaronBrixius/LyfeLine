package utils;

//Custom datetime class because "official" datetime classes we found don't have the range we need (e.g. billions of years and also milliseconds)
public class Date implements Comparable<Date> {

    private int year;
    private int month = 1;
    private int day = 1;
    private int hour;
    private int minute;
    private int second;
    private int millisecond;

    public Date() {
    }

    public Date(int year, int month, int day) {
        this(year, month, day, 0, 0, 0, 0);
    }

    public Date(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        this.year = (year == Integer.MIN_VALUE) ? 0 : year;
        this.month = Math.max(month, 1);
        this.day = Math.max(day, 1);
        this.hour = Math.max(hour, 0);
        this.minute = Math.max(minute, 0);
        this.second = Math.max(second, 0);
        this.millisecond = Math.max(millisecond, 0);
    }

    //maybe this could have been done with an imported library, but I made leap years work for fun/practice in spare time so might as well leave it in
    public int distanceTo(Date other, int scale) {
        //outputs that don't need much calculation first
        int out = other.year - this.year;
        switch (scale) {
            case 11:        //millennia
                return out / 1000;
            case 10:        //centuries
                return out / 100;
            case 9:         //decades
                return out / 10;
            case 8:         //years
                return out;
            case 7:         //months
                return out * 12 + (other.month - this.month);
        }

        //days calc
        //calculate absolute difference in days, month by month
        int startMonth = Math.min(other.month, this.month);
        int endMonth = Math.max(other.month, this.month);
        boolean secondMonthIsLater = (other.month - this.month) > 0;        //track which month comes later in the year, to either add/subtract the difference from the total later
        int diffByMonth = 0;

        for (int i = startMonth; i < endMonth; i++) {
            switch (i) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    diffByMonth += 31;
                    break;
                case 2:
                    diffByMonth += 28;          //handling leap years below
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    diffByMonth += 30;
                    break;
            }
        }
        if (!secondMonthIsLater)    //difference is negative if the end month isn't later in the year than the first
            diffByMonth *= -1;

        //leap year calc
        int leapYearsStart = this.year / 4 - this.year / 100 + this.year / 400;
        int leapYearsEnd = other.year / 4 - other.year / 100 + other.year / 400;
        int leapDays = leapYearsEnd - leapYearsStart;                  //total leap years that occur between the dates' years

        boolean startIsLeapYear = (this.year % 4 == 0 && this.year % 100 != 0) || this.year % 400 == 0;
        boolean endIsLeapYear = (other.year % 4 == 0 && other.year % 100 != 0) || other.year % 400 == 0;
        if ((startIsLeapYear && this.month < 3) && (endIsLeapYear && (other.month > 2)))    //if start and end happen within a leap year, and are opposite sides of feb 29, add the cancelled leap day in manually
            leapDays++;
        if ((endIsLeapYear && other.month < 3) && (!startIsLeapYear || (this.month > 2)))   //if end is leap year but happens before the leap day, remove it
            leapDays--;

        //convert until appropriate-to-scale output achieved, then return
        out = out * 365 + other.day - this.day + diffByMonth + leapDays;       //days

        if (scale == 5)
            return out;
        if (scale == 6)
            return out / 7;     //weeks
        out = out * 24 + other.hour - this.hour;                    //hours
        if (scale == 4)
            return out;
        out = out * 60 + other.minute - this.minute;                //minutes
        if (scale == 3)
            return out;
        out = out * 60 + other.second - this.second;                //seconds
        if (scale == 2)
            return out;
        out = out * 1000 + other.millisecond - this.millisecond;    //milliseconds
        return out;
    }


    @Override
    public String toString() {
        return "" + year + "-" + month + day + hour + minute + second + millisecond;
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
                    if (hour > o.hour)
                        return 1;
                    else if (hour < o.hour)
                        return -1;
                    else if (hour == o.hour) {
                        if (minute > o.minute)
                            return 1;
                        else if (minute < o.minute)
                            return -1;
                        else if (minute == o.minute) {
                            if (second > o.second)
                                return 1;
                            else if (second < o.second)
                                return -1;
                            else if (second == o.second) {
                                if (millisecond > o.millisecond)
                                    return 1;
                                else if (millisecond < o.millisecond)
                                    return -1;
                                else if (millisecond == o.millisecond) {
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMillisecond() {
        return millisecond;
    }

    public void setMillisecond(int millisecond) {
        this.millisecond = millisecond;
    }


}
