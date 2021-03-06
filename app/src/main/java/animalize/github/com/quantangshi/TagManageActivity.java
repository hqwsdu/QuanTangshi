package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import animalize.github.com.quantangshi.Data.TagInfo;
import animalize.github.com.quantangshi.Database.TagAgent;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class TagManageActivity extends AppCompatActivity implements View.OnClickListener {
    private List<TagInfo> mAllTagList;
    private TagContainerLayout selectTag;
    private Button renameButton, delButton;

    private TextView currentTag;
    private EditText newName;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, TagManageActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_manage);

        // toolbar
        Toolbar tb = findViewById(R.id.tag_manage_toolbar);
        setSupportActionBar(tb);

        // 要在setSupportActionBar之后
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentTag = findViewById(R.id.current_tag);

        selectTag = findViewById(R.id.select_tag);
        selectTag.setIsTagViewClickable(true);
        selectTag.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                TagInfo info = mAllTagList.get(position);
                TagManageActivity.this.currentTag.setText(info.getName());

                renameButton.setEnabled(true);
                delButton.setEnabled(true);
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int i, String s) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });

        renameButton = findViewById(R.id.rename_tag);
        renameButton.setOnClickListener(this);

        delButton = findViewById(R.id.del_tag);
        delButton.setOnClickListener(this);

        newName = findViewById(R.id.new_name);

        // 刷新标签
        refreshTags();

        if (mAllTagList.isEmpty()) {
            Toast.makeText(this, "尚未添加标签，请在添加后使用本功能。", Toast.LENGTH_LONG).show();
        }

        // 旋转恢复
        if (savedInstanceState != null) {
            String temp = savedInstanceState.getString("current", null);
            if (temp != null) {
                currentTag.setText(temp);
                renameButton.setEnabled(true);
                delButton.setEnabled(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String temp = currentTag.getText().toString();
        if (!temp.isEmpty()) {
            outState.putString("current", temp);
        }
    }

    private void refreshTags() {
        renameButton.setEnabled(false);
        delButton.setEnabled(false);

        currentTag.setText("");
        newName.setText("");

        // 所有tags 数组
        mAllTagList = TagAgent.getAllTagInfos();
        selectTag.setTags(TagAgent.getAllTagsHasCount());
    }

    @Override
    public void onClick(View v) {
        String tag = currentTag.getText().toString();
        String newname = newName.getText().toString().trim();

        switch (v.getId()) {
            case R.id.rename_tag:
                if ("".equals(newname)) {
                    Toast.makeText(this, "请先输入新的标签名称", Toast.LENGTH_SHORT).show();
                    return;
                } else if (newname.contains("'")) {
                    Toast.makeText(this, "标签不允许有单引号", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean hasNewTag = TagAgent.hasTag(newname);

                AlertDialog.Builder d = new AlertDialog.Builder(this);

                String s = hasNewTag ? "确认【合并】操作" : "确认【改名】操作";
                d.setTitle(s);

                s = hasNewTag ? " 合并到 " : " 改名为 ";
                d.setMessage("是否将 " + tag + s + newname + " ？");

                d.setCancelable(false);
                d.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tag = currentTag.getText().toString();
                        String newname = newName.getText().toString().trim();

                        TagAgent.renameTag(tag, newname);
                        refreshTags();

                        Toast.makeText(TagManageActivity.this,
                                "已执行改名、合并操作",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                d.setNegativeButton("取消", null);
                d.show();

                break;

            case R.id.del_tag:
                d = new AlertDialog.Builder(this);
                d.setTitle("确认删除标签");
                d.setMessage("是否将 " + tag + " 标签删除？");
                d.setCancelable(false);
                d.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tag = currentTag.getText().toString();

                        TagAgent.delTag(tag);
                        refreshTags();

                        Toast.makeText(TagManageActivity.this,
                                "已执行删除操作",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                d.setNegativeButton("取消", null);
                d.show();

                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
