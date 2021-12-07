package com.example.todo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.todo.database.Task
import com.example.todo.task.NewTaskActivity
import com.example.todo.task.TaskListAdapter
import com.example.todo.task.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val newTaskActivityRequestCode = 1
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val tb = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tb)

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerview)
        val adapter = TaskListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)

        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { adapter.setTasks(it) }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewTaskActivity::class.java)
            startActivityForResult(intent, newTaskActivityRequestCode)
        }

        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    target: androidx.recyclerview.widget.RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition
                    val myTask = adapter.getTaskAtPosition(pos)

                    if (direction == ItemTouchHelper.LEFT) {
                        taskViewModel.deleteTask(myTask)
                        taskViewModel.allTasks
                    }
                }

                override fun onChildDraw(
                    canvas: Canvas,
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    dimensionX: Float,
                    dimensionY: Float,
                    actionState: Int,
                    isActive: Boolean
                ) {
                    val icon: Bitmap

                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                        val itemView = viewHolder.itemView

                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3

                        val paint = Paint()
                        if (dimensionX < 0) {

                            paint.color = Color.parseColor("#CB1A1A")

                            val bground = RectF(
                                itemView.right.toFloat() + dimensionX,
                                itemView.top.toFloat(),
                                itemView.right.toFloat(),
                                itemView.bottom.toFloat()
                            )
                            canvas.drawRect(bground,paint)


                            icon = getBmFromDrawable(applicationContext, R.drawable.ic_delete_one)

                            val left = itemView.right.toFloat() - 2 * width
                            val top = itemView.top.toFloat() + width
                            val right = itemView.right.toFloat() - width
                            val bottom = itemView.bottom.toFloat() - width
                            val iconDest = RectF(left, top, right, bottom)

                            canvas.drawBitmap(icon,null,iconDest,paint)
                        }

                        super.onChildDraw(canvas, recyclerView, viewHolder, dimensionX, dimensionY, actionState, isActive)
                    }
                }

            })

        helper.attachToRecyclerView(recyclerView)

    }

    fun getBmFromDrawable(context: Context, drawableId: Int): Bitmap {
        var drawableRes = ContextCompat.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawableRes = DrawableCompat.wrap(drawableRes!!).mutate()
        }

        val bm = Bitmap.createBitmap(
            drawableRes!!.intrinsicWidth,
            drawableRes.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bm)
        drawableRes.setBounds(0, 0, canvas.width, canvas.height)
        drawableRes.draw(canvas)

        return bm
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newTaskActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val task = Task(0,data.getStringExtra(NewTaskActivity.REPLY))
                taskViewModel.insert(task)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteall -> {
                    taskViewModel.deleteAll()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
