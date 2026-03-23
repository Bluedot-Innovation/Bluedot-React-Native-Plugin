require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-pushnotifications"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                    react-native-pushnotifications
                    Note: Push Notifications support is Android-only at this time.
                   DESC
  s.homepage     = "https://github.com/Bluedot-Innovation/Bluedot-React-Native-Plugin"
  s.license = {
    :type => 'Copyright',
    :text => <<-LICENSE
    Bluedot Push Notifications SDK
    Created by Bluedot Innovation in 2026.
    Copyright © 2026 Bluedot Innovation. All rights reserved.
    LICENSE
  }
  s.author        = { "Bluedot Innovation" => "https://www.bluedot.io" }
  s.platform      = :ios, '15.1'
  s.source       = { :git => "https://github.com/Bluedot-Innovation/Bluedot-React-Native-Plugin.git" }

  # Push Notifications is Android-only. No iOS source files are included.
  s.source_files  = "ios/**/*.{h,m,swift}"
  s.requires_arc  = true

  s.dependency "React"
end
