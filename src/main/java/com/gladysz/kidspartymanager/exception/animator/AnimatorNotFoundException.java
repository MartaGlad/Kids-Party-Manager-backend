package com.gladysz.kidspartymanager.exception.animator;

public class AnimatorNotFoundException extends RuntimeException {

  public AnimatorNotFoundException(Long id) {

        super("Animator with id " + id + " not found.");
    }
}
