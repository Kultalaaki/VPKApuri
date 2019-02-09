package kultalaaki.vpkapuri;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class HalytysFragment extends Fragment {

    static DBHelper db;
    EditText halytyksentunnus, halytyksenviesti;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    public static HalytysFragment newInstance(String halytysNro) {
        HalytysFragment halytys = new HalytysFragment();
        Bundle args = new Bundle();
        args.putString("primaryKey", halytysNro);
        halytys.setArguments(args);
        return halytys;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.halytys_fragment, parent, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getArguments() != null) {
            Toast.makeText(getActivity(), "Load basic.", Toast.LENGTH_LONG).show();
        } else {
            getNewestDatabaseEntry();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        halytyksentunnus = view.findViewById(R.id.halytyksenTunnus);
        halytyksenviesti= view.findViewById(R.id.halytyksenViesti);
    }

    public void getNewestDatabaseEntry(){
        try {
            db = new DBHelper(getActivity());
            Cursor c = db.haeViimeisinLisays();
            if(c != null) {
                halytyksentunnus.setText(c.getString(c.getColumnIndex(DBHelper.TUNNUS)));
                halytyksenviesti.setText(c.getString(c.getColumnIndex(DBHelper.VIESTI)));
            }
        } catch (Exception e) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Huomautus!")
                    .setMessage("Arkisto on tyhjä. Ei näytettävää hälytystä.")
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
        }
    }
}
