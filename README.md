#Apk Update 
  # initialize your class 
    `private lateinit var download:downloadClass`

  # create  class object
    download=downloadClass(this ,"Apk Name")//Change apk name as you need
 
  # Write this code at your click listner
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
