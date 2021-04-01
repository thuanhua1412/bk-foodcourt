package com.example.bkmerchant.menu

import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bkmerchant.R
import com.example.bkmerchant.databinding.MenuItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class NewMenuAdapter(options: FirestoreRecyclerOptions<Category>,
                     val viewModel: MenuViewModel,
                     private val owner: LifecycleOwner,
                     private val storeId: String):
    FirestoreRecyclerAdapter<Category, NewMenuAdapter.MenuViewHolder>(options) {

    class MenuViewHolder private constructor(val binding: MenuItemBinding): RecyclerView.ViewHolder(binding.root) {
        private var currentAdapter: DishAdapter? = null

        fun bind(item: Category, viewModel: MenuViewModel, owner: LifecycleOwner) {
            Log.d("MenuAdapter", "Create Adapter")
            binding.category = item
            binding.viewModel = viewModel

            binding.viewText.setOnClickListener {
                val textView = (it as TextView)
                when (textView.text) {
                    textView.resources.getString(R.string.hide_all) -> {
                        textView.text = textView.resources.getString(R.string.show_all)
                        binding.dishRecycler.visibility = View.GONE
                    }
                    else -> {
                        textView.text = textView.resources.getString(R.string.hide_all)
                        binding.dishRecycler.visibility = View.VISIBLE
                    }
                }
            }

            val query: Query = FirebaseFirestore.getInstance()
                .collection("stores")
                .document(item.storeId)
                .collection("categories")
                .document(item.id)
                .collection("items")
                .orderBy("availability", Query.Direction.DESCENDING)
            val options = FirestoreRecyclerOptions.Builder<Dish>()
                .setQuery(query, Dish::class.java)
                .setLifecycleOwner(owner)
                .build()

            currentAdapter?.stopListening()

            currentAdapter = DishAdapter(options, viewModel, item)
            currentAdapter!!.startListening()

            binding.dishRecycler.adapter = currentAdapter

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    currentAdapter!!.deleteDish(viewHolder.adapterPosition)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addSwipeLeftActionIcon(R.drawable.ic_delete_24)
                        .addSwipeLeftBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color.colorSecondary
                            )
                        )
                        .addSwipeLeftLabel(binding.root.resources.getString(R.string.delete))
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate()
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

            }).attachToRecyclerView(binding.dishRecycler)
        }

        companion object {
            fun from(parent: ViewGroup): MenuViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MenuItemBinding.inflate(layoutInflater, parent, false)

                return MenuViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int, item: Category) {
        item.id = snapshots.getSnapshot(position).id
        item.storeId = storeId
        holder.bind(item, viewModel, owner)
    }
}