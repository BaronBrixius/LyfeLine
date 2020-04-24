package utils;

public class DateFormat {
    //Attributes
    private int year;
    private int month;
    private int day;
    private char punctuation;
    private char format;

    //Constructors
    public DateFormat() {
    }
    public DateFormat(int year, int month, int day, char punctuation, char format) {
        setYear(year);
        setMonth(month);
        setDay(day);
        setPunctuation(punctuation);
        setFormat(format);
    }


    //Setters
    public void setYear(int year) {
        if (year > 1899 && year < 2101)
            this.year = year;
        else
            throw new IllegalArgumentException("Year must be between 1900 and 2100.");
    }

    public void setMonth(int month) {
        if (month > 0 && month < 13)
            this.month = month;
        else
            throw new IllegalArgumentException("Month must be between 1 and 12.");
    }

    public void setDay(int day) {
        if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31)
            throw new IllegalArgumentException("Not a valid day that month.");
        else if (month == 2) {
            if (day == 30)
                throw new IllegalArgumentException("Not a valid day that month.");
            else if (day == 29 && !((year % 4 == 0 && year % 100 != 0) || year % 400 == 0))        //Leap year check
                throw new IllegalArgumentException("Not a valid day that month.");
        }
        else if (day < 1 || day > 31)
            throw new IllegalArgumentException("Day must be between 1 and 31.");

        this.day = day;
    }

    public void setPunctuation(char punctuation) {
        this.punctuation = punctuation;
    }

    public void setFormat(char format) {
        if (format == 'b' || format == 'l' || format == 'm')
            this.format = format;
        else
            this.format = 'i';
    }

    //Methods
    public String getDate(boolean fullYear) {
        //Check if valid date
        if (year == 0 || month == 0 || day == 0)
            return "Invalid Date";


        //Year format
        String yearForm;
        if (fullYear)
            yearForm = "%04d";
        else {
            year %= 100;
            yearForm = "%02d";
        }

        //Output based on date format
        String date = "";
        switch (format) {
            case 'b':
                date = String.format(yearForm, year) + punctuation + String.format("%02d", month) + punctuation + String.format("%02d", day);
                break;
            case 'l':
                date = String.format("%02d", day) + punctuation + String.format("%02d", month) + punctuation + String.format(yearForm, year);
                break;
            case 'm':
                date = String.format("%02d", month) + punctuation + String.format("%02d", day) + punctuation + String.format(yearForm, year);
                break;
            case 'i':
                date = "Invalid Date";
                break;
        }
        return date.replace("!","");  //strip ! if that was the character
    }
}

