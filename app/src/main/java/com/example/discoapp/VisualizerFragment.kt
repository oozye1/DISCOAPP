package com.example.discoapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.discoapp.EffectChipAdapter
import com.example.discoapp.databinding.FragmentVisualizerBinding
import com.example.discoapp.model.EffectRepository

/**
 * Fragment that renders audio responsive visuals. It requests the RECORD_AUDIO permission on
 * demand and, once granted, allows the user to choose between a variety of visualizer effects.
 */
class VisualizerFragment : Fragment() {

    private var _binding: FragmentVisualizerBinding? = null
    private val binding get() = _binding!!
    private val effects by lazy { EffectRepository.createVisualizerEffects(51) }
    // Launcher for requesting audio recording permission.
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted; apply the selected effect.
            applyEffect(selectedEffectIndex)
        } else {
            // Permission denied; inform the user.
            binding.visualizerView.visibility = View.INVISIBLE
        }
    }

    private var selectedEffectIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisualizerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up horizontal chip selector for effects
        val effectNames = effects.map { it.name }
        val adapter = EffectChipAdapter(effectNames, selectedEffectIndex) { index ->
            selectedEffectIndex = index
            applyEffect(index)
        }
        binding.recyclerVisualizerEffects.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerVisualizerEffects.adapter = adapter
        // Initially hide the view until permission is granted.
        binding.visualizerView.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        applyEffect(selectedEffectIndex)
    }

    override fun onPause() {
        super.onPause()
        binding.visualizerView.stop()
    }

    /**
     * Applies the visualizer effect at the given index after ensuring the RECORD_AUDIO permission
     * has been granted. If the permission is not available, it requests it.
     */
    private fun applyEffect(index: Int) {
        val context = context ?: return
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // Permission has already been granted. Show the visualizer and apply the effect.
            binding.visualizerView.visibility = View.VISIBLE
            binding.visualizerView.setEffect(effects[index])
            binding.visualizerView.start()
        } else {
            // Request permission. The result is handled in requestPermissionLauncher.
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    /**
     * Public method to set the effect by index, to be called from MainActivity.
     */
    fun setEffectByIndex(index: Int) {
        if (index in effects.indices) {
            selectedEffectIndex = index
            applyEffect(index)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = VisualizerFragment()
    }
}
