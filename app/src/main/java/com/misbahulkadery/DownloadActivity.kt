package com.misbahulkadery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.misbahulkadery.databinding.DownloadActivityBinding

/*  Developed by Misbahul kadery Prodhan
    Mail:-mk.prodhanofficial@gmail.com    */
class DownloadActivity : AppCompatActivity() {
    private lateinit var binding: DownloadActivityBinding

    private lateinit var download:downloadClass
    private lateinit var Url:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DownloadActivityBinding.inflate(layoutInflater)
        Url=""/*TODO:Change with your url*//*https://mf.edigitalpay.com/apps.apk*/
        downloadMethod()
        setContentView(binding.root)
    }

    private fun downloadMethod() {
            download=downloadClass(this ,"Apk Name")
            binding.btnUpdate.setOnClickListener({m->
                download.pathDownload(Url, object :onProgressListner{
                    override fun onStart() {
                        Toast.makeText(applicationContext, "Start", Toast.LENGTH_SHORT).show()
                    }

                    override fun onProgress(p: Int) {
                        binding.btnUpdate.text = "Updating.. $p%"
                    }

                    override fun onFinish() {
                        binding.btnUpdate.text = "Completed"
                        Toast.makeText(applicationContext, "Finish", Toast.LENGTH_SHORT).show()
                    }

                })
            })
    }
}