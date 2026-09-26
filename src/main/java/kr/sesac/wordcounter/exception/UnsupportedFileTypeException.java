package kr.sesac.wordcounter.exception;

public class UnsupportedFileTypeException extends IllegalArgumentException {

    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}