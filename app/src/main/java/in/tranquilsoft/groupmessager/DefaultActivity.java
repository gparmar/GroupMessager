package in.tranquilsoft.groupmessager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import in.tranquilsoft.groupmessager.model.impl.Contact;
import in.tranquilsoft.groupmessager.task.ContactsGathererTask;
import in.tranquilsoft.groupmessager.util.AndroidDatabaseManager;

/**
 * Created by gurdevp on 05/12/15.
 */
public abstract class DefaultActivity extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_save) {
            saveClicked();
            return true;
        } else if (id == R.id.action_refresh) {
            new Contact().deleteAll(this);
            new ContactsGathererTask(this).execute();

            return true;
        } else if (id == R.id.action_dumpdb) {
            Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
            startActivity(dbmanager);

            return true;
        } else
        if (id == R.id.action_compose) {
            editClicked();
            return true;
        } else if (id==R.id.action_history){
            historyClicked();
        }else if (id==R.id.action_debug){
            new Contact().getByContactGrpId(this, 1l);
        }

        return super.onOptionsItemSelected(item);
    }

    private void historyClicked() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem saveItem = menu.findItem(R.id.action_save);
        MenuItem editItem = menu.findItem(R.id.action_compose);
        if (!showSaveMenuOption()) {
            saveItem.setVisible(false);
        }
        if (!showEditMenuOption()) {
            editItem.setVisible(false);
        }
        //MenuItem refreshItem = menu.findItem(R.id.action_refresh);

        return true;
    }


    public abstract boolean showSaveMenuOption();

    public abstract boolean showEditMenuOption();

    //public abstract boolean showRefreshMenuOption();
    public void saveClicked() {

    }

    public void editClicked() {

    }
}
