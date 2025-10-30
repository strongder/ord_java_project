package com.ord.core.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class StringUtils {

    private StringUtils() {
    }
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String EMAIL_REGEX = "^[\\w.+\\-']+@[\\w.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    public static final String EMPTY = "";

    public static boolean isEmail(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(value).matches();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isValidCode(String input) {
        return input != null && input.matches("^[a-zA-Z0-9]*$");
    }

    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.isEmpty() || str.isBlank();
    }

    public static boolean isNotNullOrBlank(String str) {
        return !isNullOrBlank(str);
    }

    public static boolean convertBool(String input) {
        return !isNullOrEmpty(input) && ("true".equalsIgnoreCase(input) || "1".equalsIgnoreCase(input));
    }

    public static String removeMultiSpace(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", " ").trim();
    }

    public static String getRandomNum(int length) {
        final String digits = "0123456789";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(digits.length());
            sb.append(digits.charAt(index));
        }
        return sb.toString();
    }

    public static String getUuid() {
        return UUID.randomUUID().toString();
    }

    public static boolean equalsIgnoreCase(String input, String compareStr) {
        return !isNullOrEmpty(input) && input.equalsIgnoreCase(compareStr);
    }

    public static String getTransactionId(long invoiceId) {
        String idStr = String.format("%016d", invoiceId); // PadLeft(16, '0')

        String part1 = idStr.substring(0, 8);
        String part2 = idStr.substring(8, 12);
        String part3 = idStr.substring(12, 16);

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("ddMMyyHHmmssSSSS")); // tương tự "ddMMyyHHmmssffff"

        String part4 = timestamp.substring(0, 4);
        String part5 = timestamp.substring(4, 16);

        return String.format("%s-%s-%s-%s-%s", part1, part2, part3, part4, part5);
    }

    public static String removeVietnameseAccents(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String withoutAccents = pattern.matcher(normalized).replaceAll("");
        withoutAccents = withoutAccents.replace("đ", "d").replace("Đ", "D");

        return withoutAccents;
    }

    public static String convertCurrencyToWords(BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                return "Không đồng";
            }

            String currencyUnit = "đồng";
            String[] unitWords = {"", "mươi", "trăm", "nghìn", "triệu", "tỉ"};
            String[] numberWords = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
            StringBuilder result = new StringBuilder();

            // Convert to long
            long numericValue = amount.longValue();
            if (numericValue < 0) {
                result.append("Âm ");
                numericValue = Math.abs(numericValue);
            }

            String numberString = String.valueOf(numericValue);
            int length = numberString.length();
            int index = 0, hasRead = 0;

            while (index < length) {
                int groupSize = (length - index + 2) % 3 + 1;
                boolean nonZeroFound = false;

                for (int j = 0; j < groupSize; j++) {
                    if (numberString.charAt(index + j) != '0') {
                        nonZeroFound = true;
                        break;
                    }
                }

                if (nonZeroFound) {
                    hasRead = 1;
                    for (int j = 0; j < groupSize; j++) {
                        boolean addUnitWord = true;
                        char digit = numberString.charAt(index + j);
                        int currentPosition = groupSize - j - 1;

                        switch (digit) {
                            case '0':
                                if (currentPosition == 2) {
                                    result.append(numberWords[0]).append(" ");
                                } else if (currentPosition == 1 && numberString.charAt(index + j + 1) != '0') {
                                    result.append("lẻ ");
                                    addUnitWord = false;
                                }
                                break;
                            case '1':
                                if (currentPosition == 2) {
                                    result.append(numberWords[1]).append(" ");
                                } else if (currentPosition == 1) {
                                    result.append("mười ");
                                    addUnitWord = false;
                                } else if (index + j == 0 || numberString.charAt(index + j - 1) == '1' || numberString.charAt(index + j - 1) == '0') {
                                    result.append(numberWords[1]).append(" ");
                                } else {
                                    result.append("mốt ");
                                }
                                break;
                            case '5':
                                if (currentPosition == 0 && index + j > 0 && numberString.charAt(index + j - 1) != '0') {
                                    result.append("lăm ");
                                } else {
                                    result.append(numberWords[5]).append(" ");
                                }
                                break;
                            default:
                                result.append(numberWords[digit - '0']).append(" ");
                                break;
                        }

                        if (addUnitWord) {
                            result.append(unitWords[currentPosition]).append(" ");
                        }
                    }
                }

                if (length - index - groupSize > 0) {
                    int remainingDigits = length - index - groupSize;
                    if (remainingDigits % 9 == 0) {
                        if (hasRead == 1) {
                            int billionGroups = remainingDigits / 9;
                            result.append("tỉ ".repeat(Math.max(0, billionGroups)));
                        }
                        hasRead = 0;
                    } else if (nonZeroFound) {
                        result.append(unitWords[(remainingDigits % 9) / 3 + 2]).append(" ");
                    }
                }

                index += groupSize;
            }

            if (!result.isEmpty()) {
                result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
            }

            return (result.toString().trim() + " " + currencyUnit).replace("  ", " ");
        } catch (Exception ex) {
            log.error("An error occurred: ", ex);
            return "";
        }
    }

    public static boolean isValidNotAccents(String input) {
        String regex = "^[a-zA-Z0-9]*$";
        return !input.matches(regex);
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        try {
            Double.parseDouble(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static boolean hasUnicodeCharacter(String input) {
        final int MaxASCIICode = 127;

        return input.chars().anyMatch(c -> c > MaxASCIICode);
    }

    public static LocalDateTime convertToDate(String date) {
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd/M/yyyy'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd-M-yyyy'T'HH:mm:ss")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(date + "T00:00:00", formatter);
            } catch (DateTimeParseException ex) {
                log.error("An error occurred convertToDate: ", ex);
            }
        }

        return null;
    }

    public static boolean containsXSS(String input) {
        // Kiểm tra nếu input chứa các ký tự đặc biệt hay các biểu thức thường được sử dụng trong XSS
        if (input == null) {
            return false;
        }

        // Kiểm tra các ký tự nguy hiểm thường thấy trong XSS
        String[] xssPatterns = {"<", ">", "\"", "'", "&", "javascript:", "onerror", "onload", "alert("};

        // Duyệt qua các mẫu XSS và kiểm tra chúng trong input
        for (String pattern : xssPatterns) {
            if (input.toLowerCase().contains(pattern)) {
                return true; // Nếu tìm thấy mẫu XSS, trả về true
            }
        }

        return false; // Không phát hiện được mẫu XSS nào
    }



    public static String getSaveStr(String value) {
        if (isNullOrBlank(value) || isNullOrEmpty(value)) {
            return "";
        }

        return value;
    }

    public static long getInvoiceIdFormString(String input) {
        if (input == null || input.isBlank()) return 0L;

        String text = input.trim();
        try {
            // Ưu tiên: lấy dãy số ngay sau "TS" ở cuối chuỗi
            Matcher mTs = Pattern.compile("TS(\\d+)\\s*$").matcher(text);
            if (mTs.find()) {
                return Long.parseLong(mTs.group(1));
            }

            // Fallback: lấy dãy số ngay sau "INV" ở cuối chuỗi
            Matcher mInv = Pattern.compile("INV(\\d+)\\s*$").matcher(text);
            if (mInv.find()) {
                return Long.parseLong(mInv.group(1));
            }
        } catch (NumberFormatException ignore) {
            return 0L;
        }

        return 0L;
    }


    public static boolean containsAny(CharSequence cs, CharSequence... searchCharSequences) {
        return org.apache.commons.lang3.StringUtils.containsAny(cs, searchCharSequences);
    }

    public static String rightPad(String str, int size, char padChar) {
        return org.apache.commons.lang3.StringUtils.rightPad(str, size, padChar);

    }

    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        return org.apache.commons.lang3.StringUtils.equals(cs1, cs2);
    }

    public static <T extends CharSequence> T defaultIfBlank(T str, T defaultStr) {
        return org.apache.commons.lang3.StringUtils.defaultIfBlank(str, defaultStr);
    }
}
