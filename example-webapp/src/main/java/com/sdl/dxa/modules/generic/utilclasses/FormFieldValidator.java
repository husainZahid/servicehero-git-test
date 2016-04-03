package com.sdl.dxa.modules.generic.utilclasses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DELL on 1/19/2016.
 */
public class FormFieldValidator {
	private TreeMap<String, Vector<String>> fieldValidations;
	private TreeMap<String, Vector<String>> fieldSpecialCharacters;

	public static final String REQUIRED = "REQUIRED";
	public static final String ALPHABETS_ONLY = "ALPHABETS_ONLY";
	public static final String NUMBERS_ONLY = "NUMBERS_ONLY";
	public static final String ALPHA_NUMERIC = "ALPHA_NUMERIC";
	public static final String INTL_PHONE_NO = "INTL_PHONE_NO";
	public static final String EMAIL = "EMAIL";
	public static final String DATE_DD_MM_YYYY = "DATE_dd/MM/YYYY";
	public static final String DATE_MM_DD_YYYY = "DATE_MM/dd/YYYY";
	public static final String DATE_YYYY_MM_DD = "DATE_YYYYMMDD";

	public static final String MIN_LENGTH = "MIN_LENGTH";
	public static final String MAX_LENGTH = "MAX_LENGTH";
	public static final String MIN_ALPHABETS = "MIN_ALPHABETS";
	public static final String MIN_CAPITALS = "MIN_CAPITALS";
	public static final String MIN_NUMERALS = "MIN_NUMERALS";
	public static final String MIN_SPECIALS = "MIN_SPECIALS";

	private static final String REGEX_ALPHABETS_ONLY = "^[\\p{L}]+$";
	//	private static final String REGEX_NUMBERS_ONLY = "^[\\p{M}]+$";
	private static final String REGEX_NUMBERS_ONLY = "^[\\d]+$";
	private static final String REGEX_ALPHA_NUMERIC = "^[\\p{L}\\d]+$";
	private static final String REGEX_EMAIL = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
	public static final String REGEX_MOBILE = "^\\+[1-9]{1}[0-9]{1,14}$";
	private static final String ENGLISH_LETTERS = "[a-zA-z]";
	private static final String SPECIAL_CHARACTERS = "[!@#$%&*()_+=|<>?{}\\[\\]~-]";

	public static final char SPECIAL_CHARACTER_APOSTROPHE = '\'';
	public static final char SPECIAL_CHARACTER_AT = '@';
	public static final char SPECIAL_CHARACTER_COMMA = ',';
	public static final char SPECIAL_CHARACTER_HYPHEN = '-';
	public static final char SPECIAL_CHARACTER_DOT = '.';
	public static final char SPECIAL_CHARACTER_EXCLAMATION = '!';
	public static final char SPECIAL_CHARACTER_SPACE = ' ';
	public static final char SPECIAL_CHARACTER_SLASH = '/';
	public static final char SPECIAL_CHARACTER_UNDERSCORE = '_';
	public static final char SPECIAL_CHARACTER_LEFT_SQUARE_BRACKET = '[';
	public static final char SPECIAL_CHARACTER_RIGHT_SQUARE_BRACKET = ']';
	public static final char SPECIAL_CHARACTER_LEFT_PARENTHESIS = '(';
	public static final char SPECIAL_CHARACTER_RIGHT_PARENTHESIS = ')';
	public static final char SPECIAL_CHARACTER_PLUS = '+';
	public static final char SPECIAL_CHARACTER_TILDE = '~';
	public static final char SPECIAL_CHARACTER_COLON = ':';
	public static final char SPECIAL_CHARACTER_PERCENTAGE = '%';
	private static Logger logger= LoggerFactory.getLogger(FormFieldValidator.class);
	public FormFieldValidator() {
		fieldValidations = new TreeMap<String, Vector<String>>();
		fieldSpecialCharacters = new TreeMap<String, Vector<String>>();
	}

	private boolean isAllowedValidationType(String validationType) {
		return !(!validationType.equals(REQUIRED)
		         && !validationType.equals(ALPHABETS_ONLY)
		         && !validationType.equals(ALPHA_NUMERIC)
		         && !validationType.equals(NUMBERS_ONLY)
		         && !validationType.equals(EMAIL)
		         && !validationType.equals(DATE_DD_MM_YYYY)
		         && !validationType.equals(DATE_MM_DD_YYYY)
		         && !validationType.equals(INTL_PHONE_NO)
		);
	}

	private boolean isAllowedSpecialCharacter(char specialCharacter) {
		return !(specialCharacter != SPECIAL_CHARACTER_APOSTROPHE
		         && specialCharacter != SPECIAL_CHARACTER_AT
		         && specialCharacter != SPECIAL_CHARACTER_COMMA
		         && specialCharacter != SPECIAL_CHARACTER_HYPHEN
		         && specialCharacter != SPECIAL_CHARACTER_DOT
		         && specialCharacter != SPECIAL_CHARACTER_EXCLAMATION
		         && specialCharacter != SPECIAL_CHARACTER_SPACE
		         && specialCharacter != SPECIAL_CHARACTER_UNDERSCORE
		         && specialCharacter != SPECIAL_CHARACTER_LEFT_PARENTHESIS
		         && specialCharacter != SPECIAL_CHARACTER_RIGHT_PARENTHESIS
		         && specialCharacter != SPECIAL_CHARACTER_LEFT_SQUARE_BRACKET
		         && specialCharacter != SPECIAL_CHARACTER_RIGHT_SQUARE_BRACKET
		         && specialCharacter != SPECIAL_CHARACTER_PLUS
		         && specialCharacter != SPECIAL_CHARACTER_TILDE
		         && specialCharacter != SPECIAL_CHARACTER_SLASH
		         && specialCharacter !=  SPECIAL_CHARACTER_COLON
		         && specialCharacter != SPECIAL_CHARACTER_PERCENTAGE
		);
	}

	public void addSpecialCharacter(String fieldName, char specialCharacter) throws FormFieldValidatorException {
		if(isAllowedSpecialCharacter(specialCharacter)) {
			Vector<String> tempValidation;
			if(fieldSpecialCharacters.get(fieldName) != null)
				tempValidation = fieldSpecialCharacters.get(fieldName);
			else
				tempValidation = new Vector<String>();
			tempValidation.add(String.valueOf(specialCharacter));
			fieldSpecialCharacters.put(fieldName, tempValidation);
		} else {
			throw new FormFieldValidatorException("Invalid special character specified: [" + specialCharacter + "]");
		}
	}

	public void addValidation(String fieldName, String validationType) throws FormFieldValidatorException {
		if(isAllowedValidationType(validationType)) {
			Vector<String> tempValidation;
			if(fieldValidations.get(fieldName) != null)
				tempValidation = fieldValidations.get(fieldName);
			else
				tempValidation = new Vector<String>();
			tempValidation.add(validationType);
			fieldValidations.put(fieldName, tempValidation);
		} else {
			throw new FormFieldValidatorException("Invalid validation type specified: [" + validationType + "]");
		}
	}

	public void setMinLength(String fieldName, int minLength) {
		Vector<String> tempValidation;
		if(fieldValidations.get(fieldName) != null)
			tempValidation = fieldValidations.get(fieldName);
		else
			tempValidation = new Vector<String>();
		tempValidation.add("minLength:" + minLength);
		fieldValidations.put(fieldName, tempValidation);
	}

	public void setMaxLength(String fieldName, int maxLength) {
		Vector<String> tempValidation;
		if(fieldValidations.get(fieldName) != null)
			tempValidation = fieldValidations.get(fieldName);
		else
			tempValidation = new Vector<String>();
		tempValidation.add("maxLength:" + maxLength);
		fieldValidations.put(fieldName, tempValidation);
	}

	public void setMinimumNumberOfAlphabets(String fieldName, int minimumNumberOfAlphabets) {
		Vector<String> tempValidation;
		if(fieldValidations.get(fieldName) != null)
			tempValidation = fieldValidations.get(fieldName);
		else
			tempValidation = new Vector<String>();
		tempValidation.add("minimumNumberOfAlphabets:" + minimumNumberOfAlphabets);
		fieldValidations.put(fieldName, tempValidation);
	}

	public void setMinimumNumberOfCapitals(String fieldName, int minimumNumberOfCapitals) {
		Vector<String> tempValidation;
		if(fieldValidations.get(fieldName) != null)
			tempValidation = fieldValidations.get(fieldName);
		else
			tempValidation = new Vector<String>();
		tempValidation.add("minimumNumberOfCapitals:" + minimumNumberOfCapitals);
		fieldValidations.put(fieldName, tempValidation);
	}

	public void setMinimumNumberOfNumerals(String fieldName, int minimumNumberOfNumerals) {
		Vector<String> tempValidation;
		if(fieldValidations.get(fieldName) != null)
			tempValidation = fieldValidations.get(fieldName);
		else
			tempValidation = new Vector<String>();
		tempValidation.add("minimumNumberOfNumerals:" + minimumNumberOfNumerals);
		fieldValidations.put(fieldName, tempValidation);
	}

	public void setMinimumNumberOfSpecialCharacters(String fieldName, int minimumNumberOfSpecialCharacters) {
		Vector<String> tempValidation;
		if(fieldValidations.get(fieldName) != null)
			tempValidation = fieldValidations.get(fieldName);
		else
			tempValidation = new Vector<String>();
		tempValidation.add("minimumNumberOfSpecialCharacters:" + minimumNumberOfSpecialCharacters);
		fieldValidations.put(fieldName, tempValidation);
	}

	public void validate(String fieldName, String fieldValue) throws FormFieldValidatorException {
		Pattern pattern;
		Matcher matcher;
		int minLength, maxLength, minAlphabets, minCapitals, minNumerals, minSpecials;
		//		fieldValue = fieldValue.toLowerCase();
		minSpecials = 0;

		if(fieldValidations.get(fieldName) != null) {
			Vector<String> tempValidations;
			Vector<String> tempSpecials;
			tempValidations = fieldValidations.get(fieldName);

			for(String fieldValidationStr: tempValidations) {
				if(fieldValidationStr.contains("minimumNumberOfSpecialCharacters:"))
					minSpecials = Integer.parseInt(fieldValidationStr.substring("minimumNumberOfSpecialCharacters:".length()).trim());
			}

			for(String fieldValidation: tempValidations) {
				if(fieldValidation.equals(REQUIRED)) {
					if(fieldValue == null || fieldValue.length() == 0)
						throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation, fieldName, fieldValue, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
				}
				if(fieldValidation.equals(ALPHABETS_ONLY)) {
					pattern = Pattern.compile(REGEX_ALPHABETS_ONLY, Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(fieldValue);
					//					System.out.println("Field value matches the pattern: " + matcher.matches());
					String tempFieldValue;
					if(fieldSpecialCharacters.get(fieldName) != null) {
						tempFieldValue = fieldValue.replaceAll("[\\p{L}+]", "");
						//						System.out.println("Field value after replace: " + tempFieldValue);

						tempSpecials = fieldSpecialCharacters.get(fieldName);
						for(String specialCharacter:tempSpecials) {
							//							System.out.println(String.format("Removing special character [%1$s] from [%2$s]", specialCharacter, tempFieldValue));
							tempFieldValue = CStringParser.replace(tempFieldValue, specialCharacter, "");
							//							System.out.println(String.format("String after removal [%1$s]", tempFieldValue));
						}
						if(tempFieldValue.length() > 0)
							throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", "ALPHABETS_WITH_SPECIAL_CHARACTERS", fieldName, fieldValue, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
					} else {
						if(!matcher.matches())
							throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation, fieldName, fieldValue, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
					}
				}
				if(fieldValidation.equals(NUMBERS_ONLY)) {
					pattern = Pattern.compile(REGEX_NUMBERS_ONLY, Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(fieldValue);
					String tempFieldValue;
					if(fieldSpecialCharacters.get(fieldName) != null) {
						tempFieldValue = fieldValue.replaceAll("[\\d]", "");
						//						System.out.println("Field value after replace: " + tempFieldValue);

						tempSpecials = fieldSpecialCharacters.get(fieldName);
						for(String specialCharacter:tempSpecials) {
							//							System.out.println(String.format("Removing special character [%1$s] from [%2$s]", specialCharacter, tempFieldValue));
							tempFieldValue = CStringParser.replace(tempFieldValue, specialCharacter, "");
							//							System.out.println(String.format("String after removal [%1$s]", tempFieldValue));
						}
						if(tempFieldValue.length() > 0)
							throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", "NUMBERS_WITH_SPECIAL_CHARACTERS", fieldName, fieldValue, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
					} else {
						if(!matcher.matches())
							throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation, fieldName, fieldValue, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
					}
				}
				if(fieldValidation.equals(DATE_DD_MM_YYYY)) {
					try {
						// try the day format first
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
						simpleDateFormat.setLenient(false);
						simpleDateFormat.parse(fieldValue);
					} catch (ParseException e) {
						throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation, fieldName, fieldValue, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
					}
				}
				if(fieldValidation.equals(DATE_MM_DD_YYYY)) {
					try {
						// try the day format first
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
						simpleDateFormat.setLenient(false);
						simpleDateFormat.parse(fieldValue);
					} catch (ParseException e) {
						throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation, fieldName, fieldValue, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
					}
				}
				if(fieldValidation.equals(ALPHA_NUMERIC)) {
					pattern = Pattern.compile(REGEX_ALPHA_NUMERIC, Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(fieldValue);
					String tempFieldValue, tempValue;
					tempValue = fieldValue;

					int numOfSpecials;
					numOfSpecials = 0;
					char tempChar;
					try {
						for (byte tempByte : fieldValue.getBytes("UTF-8")) {
							tempChar = (char) tempByte;
							if (SPECIAL_CHARACTERS.contains(String.valueOf(tempChar))) {
								tempValue = CStringParser.replace(tempValue, tempChar, "");
								numOfSpecials++;
							}
						}
						if(numOfSpecials > 0)
							matcher = pattern.matcher(tempValue);
					} catch (UnsupportedEncodingException e) {
						//;
					}
					if(numOfSpecials < minSpecials)
						throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation + " minimumNumberOfSpecialCharacters (" + minSpecials + ")", fieldName, fieldValue + " (" + numOfSpecials + ")", DateFunctions.getDisplayDateTime( Calendar.getInstance())));

					if(fieldSpecialCharacters.get(fieldName) != null) {
						tempFieldValue = fieldValue.replaceAll("[\\d\\p{L}+]", "");
						tempSpecials = fieldSpecialCharacters.get(fieldName);
						for(String specialCharacter:tempSpecials) {
							tempFieldValue = CStringParser.replace(tempFieldValue, specialCharacter, "");
						}
						if(tempFieldValue.length() > 0)
							throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", "ALPHA_NUMERIC_WITH_SPECIAL_CHARACTERS", fieldName, fieldValue, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
					} else {
						if(!matcher.matches())
							throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation, fieldName, fieldValue, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
					}
				}
				if(fieldValidation.equals(EMAIL)) {
					pattern = Pattern.compile(REGEX_EMAIL, Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(fieldValue);
					if(!matcher.matches())
						throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] (%3$s).", fieldValidation, fieldName, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
				}
				if(fieldValidation.equals(INTL_PHONE_NO)) {
					pattern = Pattern.compile(REGEX_MOBILE, Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(fieldValue);
					if(!matcher.matches())
						throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] (%3$s).", fieldValidation, fieldName, DateFunctions.getDisplayDateTime( Calendar.getInstance())));
				}
				if(fieldValidation.startsWith("maxLength:")) {
					maxLength = Integer.parseInt(fieldValidation.substring("maxLength:".length()).trim());
					try {
						//						System.out.println("maxLength: " + maxLength + "\tfieldValue.getBytes(\"UTF-8\").length: " + fieldValue.getBytes("UTF-8").length);
						if(fieldValue == null || fieldValue.getBytes("UTF-8").length > maxLength)
							throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation, fieldName, fieldValue + " (" + fieldValue.getBytes("UTF-8").length + ")", DateFunctions.getDisplayDateTime( Calendar.getInstance())));
					} catch (UnsupportedEncodingException e) {
						//;
					}
				}
				if(fieldValidation.startsWith("minimumNumberOfAlphabets:")) {
					minAlphabets = Integer.parseInt(fieldValidation.substring("minimumNumberOfAlphabets:".length()).trim());
					int numOfLetters;
					numOfLetters = 0;
					char tempChar;
					try {
						for (byte tempByte : fieldValue.getBytes("UTF-8")) {
							tempChar = (char) tempByte;
							if (Character.isLetter(tempChar)) {
								numOfLetters++;
							}
						}
					} catch (UnsupportedEncodingException e) {
						//;
					}
					if(numOfLetters < minAlphabets)
						throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation + " (" + minAlphabets + ")", fieldName, fieldValue + " (" + numOfLetters + ")", DateFunctions.getDisplayDateTime( Calendar.getInstance())));
				}
				if(fieldValidation.startsWith("minimumNumberOfCapitals:")) {
					minCapitals = Integer.parseInt(fieldValidation.substring("minimumNumberOfCapitals:".length()).trim());
					int numOfCapitals;
					numOfCapitals = 0;
					char tempChar;
					try {
						for (byte tempByte : fieldValue.getBytes("UTF-8")) {
							tempChar = (char) tempByte;
							if (Character.isUpperCase(tempChar)) {
								numOfCapitals++;
							}
						}
					} catch (UnsupportedEncodingException e) {
						//;
					}
					if(numOfCapitals < minCapitals)
						throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation + " (" + minCapitals + ")", fieldName, fieldValue + " (" + numOfCapitals + ")", DateFunctions.getDisplayDateTime( Calendar.getInstance())));
				}
				if(fieldValidation.startsWith("minimumNumberOfNumerals:")) {
					minNumerals = Integer.parseInt(fieldValidation.substring("minimumNumberOfNumerals:".length()).trim());
					int numOfNumerals;
					numOfNumerals = 0;
					char tempChar;
					try {
						for (byte tempByte : fieldValue.getBytes("UTF-8")) {
							tempChar = (char) tempByte;
							if (Character.isDigit(tempChar)) {
								numOfNumerals++;
							}
						}
					} catch (UnsupportedEncodingException e) {
						//;
					}
					if(numOfNumerals < minNumerals)
						throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation + " (" + minNumerals + ")", fieldName, fieldValue + " (" + numOfNumerals + ")", DateFunctions.getDisplayDateTime( Calendar.getInstance())));
				}
				if(fieldValidation.startsWith("minimumNumberOfSpecialCharacters:")) {
					int numOfSpecials;
					numOfSpecials = 0;
					char tempChar;
					try {
						for (byte tempByte : fieldValue.getBytes("UTF-8")) {
							tempChar = (char) tempByte;
							if (SPECIAL_CHARACTERS.contains(String.valueOf(tempChar))) {
								numOfSpecials++;
							}
						}
					} catch (UnsupportedEncodingException e) {
						//;
					}
	/*
						try {
							for (byte tempByte : fieldValue.getBytes("UTF-8")) {
					            if (tempByte >= 33 && tempByte <= 47) {
					                numOfSpecials++;
					            }
								if (tempByte >= 58 && tempByte <= 64) {
							        numOfSpecials++;
							    }
								if (tempByte >= 91 && tempByte <= 96) {
							        numOfSpecials++;
							    }
								if (tempByte >= 123 && tempByte <= 126) {
							        numOfSpecials++;
							    }
							}
						} catch (UnsupportedEncodingException e) {
							//;
						}
	*/
					if(numOfSpecials < minSpecials)
						throw new FormFieldValidatorException(String.format("Validation [%1$s] failed for field [%2$s] with value [%3$s] (%4$s).", fieldValidation + " (" + minSpecials + ")", fieldName, fieldValue + " (" + numOfSpecials + ")", DateFunctions.getDisplayDateTime( Calendar.getInstance())));
				}
			}
		} else {
			throw new FormFieldValidatorException("No validation defined for field: [" + fieldName + "]");
		}
	}

	public static void main(String[] args) {
		try {
			FormFieldValidator formFieldValidator = new FormFieldValidator();

			formFieldValidator.addValidation("first_name", FormFieldValidator.ALPHABETS_ONLY);
			formFieldValidator.setMaxLength("first_name", 20);
			formFieldValidator.addSpecialCharacter("first_name", FormFieldValidator.SPECIAL_CHARACTER_APOSTROPHE);
			formFieldValidator.addSpecialCharacter("first_name", FormFieldValidator.SPECIAL_CHARACTER_SPACE);
			try {
				formFieldValidator.validate("first_name", "Activités");
				System.out.println("Field first_name is valid");
				logger.debug("Field first_name is valid");
				formFieldValidator.validate("first_name", "حسين علي");
				System.out.println("Field first_name is valid");
				logger.debug("Field first_name is valid");
			} catch (FormFieldValidatorException ffve) {
				logger.error("Field first_name is invalid");
				logger.error(ffve.getMessage());
				System.err.println("Field first_name is invalid");
				System.err.println(ffve.getMessage());
			}

			formFieldValidator.addValidation("mobile", FormFieldValidator.NUMBERS_ONLY);
			formFieldValidator.setMaxLength("mobile", 9);
			formFieldValidator.addSpecialCharacter("mobile", FormFieldValidator.SPECIAL_CHARACTER_HYPHEN);
			formFieldValidator.addSpecialCharacter("mobile", FormFieldValidator.SPECIAL_CHARACTER_DOT);
			try {
				formFieldValidator.validate("mobile", "1234.5678");
				System.out.println("Field mobile is valid");
				logger.debug("Field mobile is valid");
			} catch (FormFieldValidatorException ffve) {
				System.err.println("Field mobile is invalid");
				System.err.println(ffve.getMessage());
			}

			formFieldValidator.addValidation("date", FormFieldValidator.DATE_DD_MM_YYYY);
			formFieldValidator.setMaxLength("date", 10);
			try {
				formFieldValidator.validate("date", "01/01/2012");
				System.out.println("Field date is valid");
				logger.debug("Field date is valid");
				formFieldValidator.validate("date", "01/01/as");
				System.out.println("Field date is valid");
				logger.debug("Field date is valid");
			} catch (FormFieldValidatorException ffve) {
				logger.error("Field date is invalid");
				System.err.println("Field date is invalid");
				logger.error(ffve.getMessage());
				System.err.println(ffve.getMessage());
			}

			formFieldValidator.addValidation("email", FormFieldValidator.EMAIL);
			formFieldValidator.setMaxLength("email", 20);
			try {
				formFieldValidator.validate("email", "حسين@حسين.حسي");
				System.out.println("Field email is valid");
				logger.debug("Field email is valid");
			} catch (FormFieldValidatorException ffve) {
				System.err.println("Field email is invalid");
				logger.error("Field email is invalid");
				System.err.println(ffve.getMessage());
				logger.error(ffve.getMessage());
			}

			formFieldValidator.addValidation("country", FormFieldValidator.ALPHA_NUMERIC);
			formFieldValidator.setMaxLength("country", 100);
			formFieldValidator.addSpecialCharacter("country", FormFieldValidator.SPECIAL_CHARACTER_LEFT_SQUARE_BRACKET);
			formFieldValidator.addSpecialCharacter("country", FormFieldValidator.SPECIAL_CHARACTER_RIGHT_SQUARE_BRACKET);
			formFieldValidator.addSpecialCharacter("country", FormFieldValidator.SPECIAL_CHARACTER_SPACE);
			try {
				formFieldValidator.validate("country", "[965] [KW] Kuwait");
				System.out.println("Field country is valid");
				logger.debug("Field country is valid");
			} catch (FormFieldValidatorException ffve) {
				logger.error("Field country is invalid");
				System.err.println("Field country is invalid");
				logger.error(ffve.getMessage());
				System.err.println(ffve.getMessage());
			}

			formFieldValidator.addValidation("password", FormFieldValidator.REQUIRED);
			formFieldValidator.addValidation("password", FormFieldValidator.ALPHA_NUMERIC);
			formFieldValidator.setMinLength("password", 8);
			formFieldValidator.setMaxLength("password", 20);
			formFieldValidator.setMinimumNumberOfAlphabets("password", 1);
			formFieldValidator.setMinimumNumberOfNumerals("password", 1);
			formFieldValidator.setMinimumNumberOfSpecialCharacters("password", 1);
			try {
				formFieldValidator.validate("password", "P@ssw0rd");
				System.out.println("Field password is valid");
				logger.debug("Field password is valid");
			} catch (FormFieldValidatorException ffve) {
				logger.error("Field password is invalid");
				logger.error(ffve.getMessage());
				System.err.println("Field password is invalid");
				System.err.println(ffve.getMessage());
			}

			formFieldValidator.addValidation("txtDateFrom1", FormFieldValidator.DATE_DD_MM_YYYY);
			formFieldValidator.setMaxLength("txtDateFrom1", 10);
			try {
				formFieldValidator.validate("txtDateFrom1", "01/01/2012");
				System.out.println("Field txtDateFrom1 is valid");
				logger.debug("Field txtDateFrom1 is valid");
				formFieldValidator.validate("txtDateFrom1", "19/01/'+alert(12)+'");
				System.out.println("Field txtDateFrom1 is valid");
				logger.debug("Field txtDateFrom1 is valid");
			} catch (FormFieldValidatorException ffve) {
				logger.error("Field txtDateFrom1 is invalid");
				logger.error(ffve.getMessage());
				System.err.println("Field txtDateFrom1 is invalid");
				System.err.println(ffve.getMessage());
			}

			formFieldValidator.setMaxLength("comments", 10);
			try {
				formFieldValidator.validate("comments", "is a test");
				System.out.println("Field comments is valid");
				logger.debug("Field comments is valid");
				formFieldValidator.validate("comments", "this is a test for long comments");
				System.out.println("Field comments is valid");
				logger.debug("Field comments is valid");
			} catch (FormFieldValidatorException ffve) {
				logger.error("Field comments is invalid");
				logger.error(ffve.getMessage());
				System.err.println("Field comments is invalid");
				System.err.println(ffve.getMessage());
			}

			//			System.out.println("compTitle=The%2050th%20Anniversary%20of%20the%20Enactment%20of%20the%20State%20of%20Kuwait%27s%20Constitution");
			//			System.out.println(org.apache.commons.lang.StringEscapeUtils.unescapeHtml("compTitle=The%2050th%20Anniversary%20of%20the%20Enactment%20of%20the%20State%20of%20Kuwait%27s%20Constitution"));
			//			System.out.println(URLDecoder.decode("compTitle=The%2050th%20Anniversary%20of%20the%20Enactment%20of%20the%20State%20of%20Kuwait%27s%20Constitution", "UTF-8"));

		} catch(Exception e) {
			// below changes done for using Logback by Jayanthi
			logger.error(e.getMessage(),e);

			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
