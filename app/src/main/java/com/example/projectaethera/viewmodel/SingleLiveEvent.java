package com.example.projectaethera.viewmodel;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A LiveData wrapper that fires each event only once — like a snackbar or navigation signal.
 * <p>
 * I use this instead of normal {@link MutableLiveData} for things like "login succeeded"
 * or "code verified" because regular LiveData would re-trigger when the screen rotates
 * and the observer re-subscribes. That caused duplicate toasts and double navigation.
 */
public class SingleLiveEvent<T> extends MutableLiveData<T> {

    /** Tracks whether a new value is waiting to be delivered to observers. */
    private final AtomicBoolean mPending = new AtomicBoolean(false);

    /**
     * Observes this event but only calls the observer when a fresh value was posted.
     *
     * @param owner lifecycle owner (usually the Activity or Fragment)
     * @param observer callback that runs once per posted event
     */
    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<? super T> observer){
        super.observe(owner, t -> {
            if (mPending.compareAndSet(true, false)){
                observer.onChanged(t);
            }
        });
    }

    /**
     * Marks the next value as pending so observers will receive it exactly once.
     *
     * @param t the event payload (can be null for fire-and-forget events)
     */
    @MainThread
    public void setValue(@NonNull T t){
        mPending.set(true);
        super.setValue(t);
    }


    /**
     * Fires a one-shot event with no payload — handy for "navigate now" signals.
     */
    @MainThread
    public  void  call(){
        setValue(null);
    }
}
