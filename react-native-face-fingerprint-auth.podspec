require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name           = package['name']
  s.version        = package['version']
  s.summary        = package['summary']
  s.description    = package['description']
  s.description    = package['description']
  s.license        = package['license']
  s.authors        = { "author" => "author@domain.cn" }
  s.homepage	   = 'https://google.com/'
  s.source         = { :git => 'https://github.com/josuecruz/react-native-face-fingerprint-auth.git', :tag => '0.0.1' }
  s.platform       = :ios, '10.0'
  s.source_files   = 'ios/**/*.{h,m}'
  s.dependency     'React-Core'
end
