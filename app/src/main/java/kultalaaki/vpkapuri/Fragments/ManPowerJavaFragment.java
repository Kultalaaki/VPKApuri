package kultalaaki.vpkapuri.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kultalaaki.vpkapuri.R;
import kultalaaki.vpkapuri.dbfirealarm.FireAlarmViewModel;

@Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \u00182\u00020\u0001:\u0002\u0018\u0019B\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016J\u0010\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0012\u0010\u0010\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016J&\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016J\b\u0010\u0017\u001a\u00020\nH\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001a"}, d2 = {"Lkultalaaki/vpkapuri/Fragments/ManpowerFragment;", "Landroidx/fragment/app/Fragment;", "()V", "fireAlarmViewModel", "Lkultalaaki/vpkapuri/FireAlarmViewModel;", "mListener", "Lkultalaaki/vpkapuri/Fragments/ManpowerFragment$OnFragmentInteractionListener;", "mParam1", "", "onActivityCreated", "", "savedInstanceState", "Landroid/os/Bundle;", "onAttach", "activity", "Landroid/app/Activity;", "onCreate", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "onDetach", "Companion", "OnFragmentInteractionListener", "app_debug"})
public final class ManPowerJavaFragment extends Fragment {
    private String mParam1;
    private FireAlarmViewModel fireAlarmViewModel;
    private OnFragmentInteractionListener mListener;
    private static final String ARG_PARAM1 = "param1";
    @NotNull
    public static final ManPowerJavaFragment.Companion Companion = new ManPowerJavaFragment.Companion((DefaultConstructorMarker) null);

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() != null) {
            this.mParam1 = this.requireArguments().getString(ARG_PARAM1);
        }

    }

    @Nullable
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intrinsics.checkNotNullParameter(inflater, "inflater");
        return inflater.inflate(R.layout.fragment_manpower, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onAttach(@NotNull Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        super.onAttach(activity);
        if (this.getContext() instanceof ManPowerJavaFragment.OnFragmentInteractionListener) {
            Context var10001 = this.getContext();
            if (var10001 == null) {
                throw new NullPointerException("null cannot be cast to non-null type kultalaaki.vpkapuri.Fragments.ManpowerFragment.OnFragmentInteractionListener");
            } else {
                this.mListener = (ManPowerJavaFragment.OnFragmentInteractionListener) var10001;
            }
        } else {
            try {
                throw (Throwable) (new RuntimeException(this.requireContext().toString() + " must implement OnFragmentInteractionListener"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void onDetach() {
        super.onDetach();
        this.mListener = (ManPowerJavaFragment.OnFragmentInteractionListener) null;
    }

    // $FF: synthetic method
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006"}, d2 = {"Lkultalaaki/vpkapuri/Fragments/ManpowerFragment$OnFragmentInteractionListener;", "", "onFragmentInteraction", "", "uri", "Landroid/net/Uri;", "app_debug"})
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(@NotNull Uri var1);
    }

    @Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D¢\u0006\u0002\n\u0000¨\u0006\b"}, d2 = {"Lkultalaaki/vpkapuri/Fragments/ManpowerFragment$Companion;", "", "()V", "ARG_PARAM1", "", "newInstance", "Lkultalaaki/vpkapuri/Fragments/ManpowerFragment;", "param1", "app_debug"})
    public static final class Companion {
        @NotNull
        public final ManPowerJavaFragment newInstance(@NotNull String param1) {
            Intrinsics.checkNotNullParameter(param1, "param1");
            ManPowerJavaFragment fragment = new ManPowerJavaFragment();
            Bundle args = new Bundle();
            args.putString(ManPowerJavaFragment.ARG_PARAM1, param1);
            fragment.setArguments(args);
            return fragment;
        }

        private Companion() {
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
