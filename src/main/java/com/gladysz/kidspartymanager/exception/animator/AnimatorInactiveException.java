package com.gladysz.kidspartymanager.exception.animator;

public class AnimatorInactiveException extends RuntimeException {

    public AnimatorInactiveException(Long id) {

        super("Animator with id " + id + " is inactive.");
    }
}
