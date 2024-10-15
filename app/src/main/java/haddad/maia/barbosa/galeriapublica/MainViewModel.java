package haddad.maia.barbosa.galeriapublica;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


public class MainViewModel extends AndroidViewModel {
    int navigationOpSelected = R.id.gridViewOp;
    public MainViewModel(@NonNull Application application){
        super(application);
    }
    public int getNavigationOpSelected(){
        return navigationOpSelected
    }
    public void setNavigationOpSelected(){
        this.navigationOpSelected = navigationOpSelected;
    }
}
