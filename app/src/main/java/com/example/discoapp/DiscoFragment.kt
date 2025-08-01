package com.example.discoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.discoapp.databinding.FragmentDiscoBinding
import com.example.discoapp.model.DiscoEffect
import com.example.discoapp.model.EffectRepository

/**
 * Fragment that hosts the disco lighting experience. A [Spinner] allows the user to select
 * from a collection of generated [DiscoEffect]s. The selected effect is applied to the
 * [DiscoView] filling the remaining space. Effects are built algorithmically and vary in
 * palette, speed, fade transitions and strobing.
 */
class DiscoFragment : Fragment() {

    private var _binding: FragmentDiscoBinding? = null
    private val binding get() = _binding!!

    private val effects by lazy { EffectRepository.createDiscoEffects(50) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Populate the spinner with effect names.
        val effectNames = effects.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, effectNames)
        binding.spinnerDiscoEffects.adapter = adapter
        // When the user selects an effect apply it to the DiscoView.
        binding.spinnerDiscoEffects.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val effect = effects[position]
                binding.discoView.setEffect(effect)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No op.
            }
        }
        // Initially apply the first effect.
        if (effects.isNotEmpty()) {
            binding.discoView.setEffect(effects[0])
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = DiscoFragment()
    }
}
