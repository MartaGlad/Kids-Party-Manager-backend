package com.gladysz.kidspartymanager.exception.animator;

public class AnimatorDeleteException extends RuntimeException {

    public AnimatorDeleteException(Long id) {

        super("Animator with id " + id +
                " cannot be deleted because it is assigned to existing reservations.");
    }
}
